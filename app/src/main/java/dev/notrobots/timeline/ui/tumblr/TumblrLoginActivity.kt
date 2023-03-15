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
import dev.notrobots.androidstuff.extensions.toStringOrEmpty
import dev.notrobots.androidstuff.extensions.viewBindings
import dev.notrobots.androidstuff.util.Logger
import dev.notrobots.timeline.data.TUMBLR_USER_AGENT
import dev.notrobots.timeline.databinding.ActivityTwitterLoginBinding
import dev.notrobots.timeline.db.ProfileDao
import dev.notrobots.timeline.models.Profile
import dev.notrobots.timeline.models.Socials
import dev.notrobots.timeline.util.SocialManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dean.jraw.android.SharedPreferencesTokenStore
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class TumblrLoginActivity : AppCompatActivity() {
    //TODO: Either create one for each login activity or a generic one since it's just a webview
    private val binding by viewBindings<ActivityTwitterLoginBinding>()
    private val logger = Logger(this)

    @Inject
    protected lateinit var profileDao: ProfileDao

    @Inject
    protected lateinit var http: OkHttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val tumblrHelper = SocialManager.tumblrHelper()

        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()

        binding.webView.clearCache(true)
        binding.webView.clearHistory()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                if (tumblrHelper.isFinalRequestUrl(url)) {
                    D

                    lifecycleScope.launch(Dispatchers.Default) {
                        try {
                            val code = tumblrHelper.getCode(url)
                            val accessToken = tumblrHelper.requestAccessToken(code)

                            requireNotNull(accessToken) {
                                "Access token is null"
                            }

                            val userRequest = Request.Builder()
                                .url("https://api.tumblr.com/v2/user/info")
                                .header("Authorization", "Bearer ${accessToken.accessToken}")
                                .header("User-Agent", TUMBLR_USER_AGENT)
                                .build()
                            val userResponse = http.newCall(userRequest).execute()

//                        val tumblrClient = JumblrClient(
//                            TUMBLR_CONSUMER_KEY,
//                            TUMBLR_CONSUMER_SECRET
//                        )

//                        tumblrClient.requestBuilder.

                            val username = JSONObject(userResponse.body?.string() ?: "")
                                .getJSONObject("response")
                                .getJSONObject("user")
                                .getString("name")

                            // If the user is already present update the token

                            val profile = profileDao.getProfile(username, Socials.Tumblr)

                            logger.logi("Logged user: $username")

                            if (profile != null) {
                                //xxx: Refresh token if needed?
                                tumblrHelper.tokenStore.store(profile.toString(), accessToken)
                                setResult(RESULT_ALREADY_LOGGED_IN)
                            } else {
                                val newProfile = Profile(
                                    username,
                                    Socials.Tumblr
                                )

                                profileDao.insert(newProfile).also {
                                    newProfile.profileId = it
                                }
                                tumblrHelper.tokenStore.store(newProfile.toString(), accessToken)
                                setResult(RESULT_OK)
                            }
                        } catch (e: Exception) {
                            setResult(RESULT_CANCELED)
                            logger.loge("OAuth2 error", e)
                        } finally {
                            finish()
                        }
                    }
                }

                lifecycleScope.launch(Dispatchers.Default) {

                }
            }
        }
        binding.webView.loadUrl(tumblrHelper.authorizationUrl)
    }

    fun WebView.injectJs(code: String) {
        loadUrl("javascript:(function(){$code})()")
    }

    companion object {
        const val RESULT_CANCELED = 0
        const val RESULT_OK = 100
        const val RESULT_ALREADY_LOGGED_IN = 200
        const val RESULT_ERROR = 300
    }
}