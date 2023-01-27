package dev.notrobots.timeline.data

import dev.notrobots.preferences2.annotations.BooleanPreference

object Preferences {
    @BooleanPreference(true)
    const val FILTER_FAKE_REDDIT_ENABLED = "filter_fake_reddit_enabled"
    @BooleanPreference(true)
    const val FILTER_FAKE_TWITTER_ENABLED = "filter_fake_twitter_enabled"
}