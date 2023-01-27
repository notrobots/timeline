package dev.notrobots.timeline.extensions

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.notrobots.timeline.models.Profile

fun SharedPreferences.isProfileEnabled(profile: Profile): Boolean {
    return getBoolean("${profile.social.lowercase()}_${profile.username}", true)
}

fun SharedPreferences.setProfileEnabled(profile: Profile, value: Boolean) {
    return edit { putBoolean("${profile.social.lowercase()}_${profile.username}", value) }
}