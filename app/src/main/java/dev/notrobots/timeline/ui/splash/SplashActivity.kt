package dev.notrobots.timeline.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.notrobots.androidstuff.extensions.startActivity
import dev.notrobots.androidstuff.util.Logger
import dev.notrobots.timeline.data.TUMBLR_USER_AGENT
import dev.notrobots.timeline.db.ProfileDao
import dev.notrobots.timeline.models.Socials
import dev.notrobots.timeline.ui.timeline.TimelineActivity
import dev.notrobots.timeline.util.SocialManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val logger = Logger(this)

    @Inject
    protected lateinit var profileDao: ProfileDao

    @Inject
    protected lateinit var http: OkHttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.Default) {
            SocialManager.init(this@SplashActivity, profileDao.getProfiles())



            // TEST
            SocialManager.defaultTokenStore.ids.filter {
                it.endsWith(Socials.Tumblr)
            }.forEach {
                try {
                    val accessToken = SocialManager.defaultTokenStore.fetch(it)
                    val userRequest = Request.Builder()
                        .url("https://api.tumblr.com/v2/user/info")
                        .header("Authorization", "Bearer ${accessToken?.accessToken}")
                        .header("User-Agent", TUMBLR_USER_AGENT)
                        .build()
                    val userResponse = http.newCall(userRequest).execute()
                    val username = JSONObject(userResponse.body?.string() ?: "")
                        .getJSONObject("response")
                        .getJSONObject("user")
                        .getString("name")

                    logger.logi("Logged as Tumblr user $username")
                } catch (e: Exception) {
                    logger.loge("Cannot fetch Tumblr user for token id $it. Token might be expired.")
                }
            }

            startActivity(TimelineActivity::class)
            finish()
        }
    }
}