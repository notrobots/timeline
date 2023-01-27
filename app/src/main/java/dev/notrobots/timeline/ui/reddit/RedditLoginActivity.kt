package dev.notrobots.timeline.ui.reddit

import android.app.Activity
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
import dev.notrobots.timeline.App
import dev.notrobots.timeline.databinding.ActivityRedditLoginBinding
import dev.notrobots.timeline.models.Profile
import dev.notrobots.timeline.db.ProfileDao
import dev.notrobots.timeline.models.Socials
import dev.notrobots.timeline.util.SocialManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RedditLoginActivity : AppCompatActivity() {
    private val binding by viewBindings<ActivityRedditLoginBinding>()

    @Inject
    protected lateinit var profileDao: ProfileDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val accountHelper = SocialManager.redditAccountHelper(this)
        val helper = accountHelper.switchToNewUser()
        val authUrl = helper.getAuthorizationUrl(true, true, "read", "identity")

        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()

        binding.webView.clearCache(true)
        binding.webView.clearHistory()
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                if (helper.isFinalRedirectUrl(url)) {
                    view.stopLoading()
                    view.isGone = true

                    lifecycleScope.launch(Dispatchers.Default) {
                        try {
                            helper.onUserChallenge(url)

                            val username = accountHelper.reddit.me().username

                            if (profileDao.exists(username, Socials.Reddit)) {
                                setResult(RESULT_ALREADY_LOGGED_IN)
                            } else {
                                val newProfile = Profile(
                                    username,
                                    Socials.Reddit
                                )

                                profileDao.insert(newProfile).also {
                                    newProfile.profileId = it
                                }

                                SocialManager.redditAddNewProfile(newProfile, accountHelper)

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
        }
        binding.webView.loadUrl(authUrl)
    }

    companion object {
        const val RESULT_CANCELED = 0
        const val RESULT_OK = 100
        const val RESULT_ALREADY_LOGGED_IN = 200
    }
}