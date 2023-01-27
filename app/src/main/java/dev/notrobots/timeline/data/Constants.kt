package dev.notrobots.timeline.data

import dev.notrobots.timeline.App

//region Reddit

const val REDDIT_DEV_USERNAME = "notrobots-dev"
val REDDIT_USER_AGENT = "android:${App.PACKAGE_NAME}:${App.VERSION} (by /u/$REDDIT_DEV_USERNAME)"

//endregion

//region Twitter

const val TWITTER_DEV_USERNAME = "NotRobotsDev"
val TWITTER_USER_AGENT = "android:${App.PACKAGE_NAME}:${App.VERSION} (by @$TWITTER_DEV_USERNAME)"

//endregion