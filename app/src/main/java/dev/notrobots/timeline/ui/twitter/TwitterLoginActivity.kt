package dev.notrobots.timeline.ui.twitter

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.notrobots.androidstuff.extensions.viewBindings
import dev.notrobots.androidstuff.util.Logger
import dev.notrobots.timeline.data.DEVICE_UUID_STRING
import dev.notrobots.timeline.data.TWITTER_USER_AGENT
import dev.notrobots.timeline.databinding.ActivityTwitterLoginBinding
import dev.notrobots.timeline.db.ProfileDao
import dev.notrobots.timeline.models.Profile
import dev.notrobots.timeline.models.Socials
import dev.notrobots.timeline.ui.reddit.RedditLoginActivity
import dev.notrobots.timeline.util.SocialManager
import io.github.redouane59.twitter.TwitterClient
import io.github.redouane59.twitter.signature.TwitterCredentials
import io.github.redouane59.twitter.signature.TwitterCredentials.TwitterCredentialsBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.http.UserAgent
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class TwitterLoginActivity : AppCompatActivity() {
    private val binding by viewBindings<ActivityTwitterLoginBinding>()
    private val logger = Logger(this)

    @Inject
    protected lateinit var profileDao: ProfileDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        // https://twitter.com/i/oauth2/authorize?
        // response_type=code&
        // client_id=TWITTER_CLIENT_ID&
        // redirect_uri=https://www.example.com&
        // scope=tweet.read%20users.read%20follows.read%20follows.write&
        // state=state&
        // code_challenge=challenge&
        // code_challenge_method=plain

        val request = HttpRequest.Builder()
            .secure()
            .host("www.twitter.com")
            .path("/i/oauth2/authorize")
            .query(
                mapOf(
                    "response_type" to "code",
                    "client_id" to "TWITTER_CLIENT_ID",
                    "redirect_uri" to "http://localhost:8080",
                    "scope" to "tweet.read%20users.read%20follows.read%20follows.write",
                    "state" to "state", //xxx ???
                    "code_challenge" to DEVICE_UUID_STRING,
                    "code_challenge_method" to "plain"
                )
            )
            .build()

        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()

        binding.webView.clearCache(true)
        binding.webView.clearHistory()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                val url = url.toHttpUrlOrNull()

                // Skip until the url is the same as the redirect uri
                if (url == null || !url.toString().startsWith("http://localhost:8080")) {
                    return
                }

                val error = url.queryParameter("error")

                if (error != null) {
                    setResult(500)
                    logger.loge("OAuth2 error: $error")
                    finish()
                    return
                }

                val code = url.queryParameter("code")
                val state = url.queryParameter("state")

                if (state == null) {    // TODO: Check that saved state (the one passed when creating the authUrl) is the same as this
                    setResult(600)
                    logger.loge("Query parameter 'state' is missing")
                    finish()
                    return
                }

                if (code == null) {
                    setResult(700)
                    logger.loge("Query parameter 'code' is missing")
                    finish()
                    return
                }

                view.stopLoading()
                view.isGone = true

                lifecycleScope.launch(Dispatchers.Default) {
                    try {
                        val http = OkHttpNetworkAdapter(UserAgent(TWITTER_USER_AGENT), OkHttpClient())
                        val tokenRequest = HttpRequest.Builder()
                            .secure(true)
                            .host("www.twitter.com")
                            .path("/2/oauth2/token")
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .post(
                                mapOf(
                                    "code" to code,
                                    "grant_type" to "authorization_code",
                                    "redirect_uri" to "http://localhost:8080",
                                    "code_verifier" to DEVICE_UUID_STRING,
                                    "client_id " to "TWITTER_CLIENT_ID"

                                )
                            )
                            .basicAuth("TWITTER_CLIENT_ID" to "")
                            .build()

                        val tokenResponse = http.execute(tokenRequest)
                        val json = JSONObject(tokenResponse.body)

                        val twitterClient = TwitterClient(
                            TwitterCredentials.builder()
                                .accessToken(json.getString("token"))
                                .build()
                        )

                        val userId = twitterClient.userIdFromAccessToken
                        val user = twitterClient.getUserFromUserId(userId)
                        val username = user.name

                        if (profileDao.exists(username, Socials.Twitter)) {
                            //xxx: Refresh token if needed?
                            setResult(RESULT_ALREADY_LOGGED_IN)
                        } else {
                            val newProfile = Profile(
                                username,
                                Socials.Twitter,
                                true,
                                "@"
                            )

                            profileDao.insert(newProfile).also {
                                newProfile.profileId = it
                            }

//                            SocialManager.twitterAddNewProfile(newProfile)
                            setResult(RESULT_OK)
                        }
                    } catch (_: Exception) {
                        setResult(RESULT_CANCELED)
                    } finally {
                        finish()
                    }
                }
            }
        }
        binding.webView.loadUrl(request.url)
    }

    companion object {
        const val RESULT_CANCELED = 0
        const val RESULT_OK = 100
        const val RESULT_ALREADY_LOGGED_IN = 200
        const val RESULT_ERROR = 300
    }
}