package dev.notrobots.timeline.ui.tumblr

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tumblr.jumblr.request.TumblrAuthHelper
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
import okhttp3.OkHttpClient
import java.util.UUID
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

        val helper = SocialManager.tumblrHelper()
        val state = UUID.randomUUID().toString()

        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()

        binding.webView.clearCache(true)
        binding.webView.clearHistory()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                if (helper.isFinalRequestUrl(url)) {
                    lifecycleScope.launch(Dispatchers.Default) {
                        try {
                            helper.onUserChallenge(url, state)
                            val username = helper.client.user().name

                            logger.logi("Logged user: $username")

                            if (profileDao.exists(username, Socials.Tumblr)) {
                                //xxx: Refresh token if needed?
//                                tumblr.tokenStore.store(profile.toString(), accessToken)
                                setResult(RESULT_ALREADY_LOGGED_IN)
                            } else {
                                val profile = Profile(
                                    username,
                                    Socials.Tumblr,
                                    true,
                                    helper.client.clientId
                                )

                                profile.profileId = profileDao.insert(profile)
                                SocialManager.tumblrAddProfile(profile, helper)
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
            }
        }
        binding.webView.loadUrl(helper.authService.getAuthorizationUrl(state))
    }

    companion object {
        const val RESULT_CANCELED = 0
        const val RESULT_OK = 100
        const val RESULT_ALREADY_LOGGED_IN = 200
        const val RESULT_ERROR = 300
    }
}