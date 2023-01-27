package dev.notrobots.timeline.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.notrobots.timeline.R
import dev.notrobots.timeline.db.ProfileDao
import dev.notrobots.timeline.models.Profile
import dev.notrobots.timeline.util.SocialManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LogoutProfileDialog(
    private var profile: Profile? = null
) : DialogFragment() {
    @Inject
    protected lateinit var profileDao: ProfileDao

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        savedInstanceState?.let {
            profile = it.getSerializable(SAVED_STATE_PROFILE) as Profile
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.label_logout_profile)
            .setMessage(R.string.label_logout_profile_body)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                profile?.let {
                    requireActivity().lifecycleScope.launch(Dispatchers.Default) {
                        SocialManager.redditLogout(it)
                        profileDao.delete(it)
                    }
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable(SAVED_STATE_PROFILE, profile)
    }

    companion object {
        private const val SAVED_STATE_PROFILE = "LogoutProfileDialog.profile"
    }
}