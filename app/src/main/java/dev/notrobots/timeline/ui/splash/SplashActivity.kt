package dev.notrobots.timeline.ui.splash

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.notrobots.androidstuff.extensions.startActivity
import dev.notrobots.timeline.db.ProfileDao
import dev.notrobots.timeline.ui.timeline.TimelineActivity
import dev.notrobots.timeline.util.SocialManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    @Inject
    protected lateinit var profileDao: ProfileDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.Default) {
            SocialManager.init(this@SplashActivity, profileDao.getProfiles())

            startActivity(TimelineActivity::class)
            finish()
        }
    }
}