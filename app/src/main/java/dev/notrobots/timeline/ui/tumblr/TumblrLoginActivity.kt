package dev.notrobots.timeline.ui.tumblr

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
import dev.notrobots.timeline.data.TUMBLR_CONSUMER_KEY
import dev.notrobots.timeline.data.TUMBLR_CONSUMER_SECRET
import dev.notrobots.timeline.data.TUMBLR_REDIRECT_URI
import dev.notrobots.timeline.data.TUMBLR_USER_AGENT
import dev.notrobots.timeline.databinding.ActivityTwitterLoginBinding
import dev.notrobots.timeline.db.ProfileDao
import dev.notrobots.timeline.models.Profile
import dev.notrobots.timeline.models.Socials
import dev.notrobots.timeline.util.SocialManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.http.UserAgent
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import org.apache.http.HttpHeaders.CONTENT_TYPE
import org.apache.http.HttpHeaders.USER_AGENT
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class TumblrLoginActivity : AppCompatActivity() {
    //TODO: Either create one for each login activity or a generic one since it's just a webview
    private val binding by viewBindings<ActivityTwitterLoginBinding>()
    private val logger = Logger(this)

    @Inject
    protected lateinit var profileDao: ProfileDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val http = OkHttpNetworkAdapter(UserAgent(TUMBLR_USER_AGENT), OkHttpClient())
        val oauth2State = SocialManager.deviceUuid.toString()

        val authorizationRequest = dev.notrobots.androidstuff.networking.HttpRequest.Builder()
            .secure()
            .host("www.tumblr.com")
            .path("/oauth2/authorize")
            .header(USER_AGENT, TUMBLR_USER_AGENT)
            .header(CONTENT_TYPE, "application/x-www-form-urlencoded")
            .query(
                mapOf(
                    "client_id" to TUMBLR_CONSUMER_KEY,
                    "response_type" to "code",   // It's always code, source: https://www.tumblr.com/docs/en/api/v2#oauth2authorize---authorization-request
                    "scope" to "basic",  // basic, write, offline_access (needed for refresh token)
                    "state" to oauth2State,
//                    "redirect_uri" to TUMBLR_REDIRECT_URI
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
                if (url == null || !url.toString().startsWith(TUMBLR_REDIRECT_URI)) {
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

                if (code == null) {
                    setResult(RESULT_ERROR)
                    logger.loge("Query parameter 'code' is missing")
                    finish()
                    return
                }

                if (state == null) {
                    setResult(RESULT_ERROR)
                    logger.loge("Query parameter 'state' is missing")
                    finish()
                    return
                }

                if (state != oauth2State) {
                    setResult(RESULT_ERROR)
                    logger.loge("State doesn't match")
                    finish()
                    return
                }

                view.stopLoading()
                view.isGone = true

                lifecycleScope.launch(Dispatchers.Default) {
                    try {
                        // https://www.tumblr.com/oauth/access_token
                        val tokenRequest = HttpRequest.Builder()
                            .secure()
                            .host("api.tumblr.com")
                            .path("/v2/oauth2/token")
                            .header(USER_AGENT, TUMBLR_USER_AGENT)
                            .header(CONTENT_TYPE, "application/x-www-form-urlencoded")
                            .post(
                                mapOf(
                                    "grant_type" to "authorization_code",
                                    "code" to code,
                                    "client_id" to TUMBLR_CONSUMER_KEY,
                                    "client_secret" to TUMBLR_CONSUMER_SECRET,
//                                    "redirect_uri" to TUMBLR_REDIRECT_URI
                                )
                            )
                            .build()

                        val tokenResponse = http.execute(tokenRequest)

                        if (tokenResponse.code != 200) {
                            logger.loge("Token response code: ${tokenResponse.code}")
                            logger.loge("Token response: ${tokenResponse.body}")
                            setResult(RESULT_ERROR)
                            finish()
                            return@launch
                        }

                        val json = JSONObject(tokenResponse.body)

                        logger.logi("Token response: $json")

                        val accessToken = json.getString("access_token")

                        val userRequest = HttpRequest.Builder()
                            .secure()
                            .host("api.tumblr.com")
                            .path("/v2/user/info")
                            .header(USER_AGENT, TUMBLR_USER_AGENT)
                            .header(CONTENT_TYPE, "application/x-www-form-urlencoded")
                            .header("Authorization", "Bearer $accessToken")
                            .build()
                        val userResponse = http.execute(userRequest)

//                        val tumblrClient = JumblrClient(
//                            TUMBLR_CONSUMER_KEY,
//                            TUMBLR_CONSUMER_SECRET
//                        )

//                        tumblrClient.requestBuilder.

                        val username = JSONObject(userResponse.body)
                            .getJSONObject("response")
                            .getJSONObject("user")
                            .getString("name")

                        logger.logi("Logged user: $username")

                        if (profileDao.exists(username, Socials.Tumblr)) {
                            //xxx: Refresh token if needed?
                            setResult(RESULT_ALREADY_LOGGED_IN)
                        } else {
                            val newProfile = Profile(
                                username,
                                Socials.Tumblr
                            )

                            profileDao.insert(newProfile).also {
                                newProfile.profileId = it
                            }

                            SocialManager.tumblrAddNewProfile(newProfile)
                            setResult(RESULT_OK)
                        }
                    } catch (e: Exception) {
                        setResult(RESULT_CANCELED)
                        logger.loge("Login error", e)
                    } finally {
                        finish()
                    }
                }
            }
        }
        binding.webView.loadUrl(authorizationRequest.url)
    }

    companion object {
        const val RESULT_CANCELED = 0
        const val RESULT_OK = 100
        const val RESULT_ALREADY_LOGGED_IN = 200
        const val RESULT_ERROR = 300
    }
}