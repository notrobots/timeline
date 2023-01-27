package dev.notrobots.timeline.ui.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import dagger.hilt.android.AndroidEntryPoint
import dev.notrobots.androidstuff.extensions.makeToast
import dev.notrobots.preferences2.fragments.MaterialPreferenceFragment
import dev.notrobots.timeline.App
import dev.notrobots.timeline.R
import dev.notrobots.timeline.db.ProfileDao
import dev.notrobots.timeline.dialogs.LogoutProfileDialog
import dev.notrobots.timeline.ui.reddit.RedditLoginActivity
import dev.notrobots.timeline.util.SocialManager
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SettingsFragment())
            .commit()
    }

    @AndroidEntryPoint
    class SettingsFragment : MaterialPreferenceFragment() {
        private val redditLoginRequest = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                RedditLoginActivity.RESULT_OK -> requireContext().makeToast("Logged in!")
                RedditLoginActivity.RESULT_ALREADY_LOGGED_IN -> requireContext().makeToast("Logged in!")

                else -> requireContext().makeToast("Error!")

            }
        }
        private val profilesCategoryPref by lazy {
            findPreference<PreferenceCategory>("_profiles")
        }

        @Inject
        protected lateinit var profileDao: ProfileDao

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.pref_settings)

            profileDao.getProfilesLive().observe(this) {
                profilesCategoryPref?.removeAll()

                for (profile in it) {
                    val preference = CheckBoxPreference(requireContext())

                    preference.key = profile.social + profile.username
                    preference.title = profile.social
                    preference.summary = profile.username
                    preference.setOnPreferenceClickListener {
                        LogoutProfileDialog(profile)
                            .show(parentFragmentManager, null)

                        true
                    }

                    profilesCategoryPref?.addPreference(preference)
                }
            }

            findPreference<Preference>("_reddit_login")?.setOnPreferenceClickListener {
//                if (SocialManager.redditTokenStore.size() > 0) {
//                    if (SocialManager.redditAccountHelper.isAuthenticated()) {
//                        requireContext().makeToast("Already logged in and authenticated")
//                    } else {
//                        val username = SocialManager.redditTokenStore.data().keys.elementAt(0)
//
//                        SocialManager.redditAccountHelper.switchToUser(username)
//
//                        requireContext().makeToast("Already logged in. Authenticating..")
//                    }
//                } else {
                    redditLoginRequest.launch(Intent(requireContext(), RedditLoginActivity::class.java))
//                }

                true
            }

            findPreference<Preference>("_twitter_login")?.setOnPreferenceClickListener {
                requireContext().makeToast("Twitter login not supported yet")
                true
            }

            findPreference<Preference>("_tumblr_login")?.setOnPreferenceClickListener {
                requireContext().makeToast("Tumblr login not supported yet")
                true
            }
        }
    }
}