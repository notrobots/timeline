package dev.notrobots.timeline.data

import dev.notrobots.timeline.App
import java.util.*

val DEVICE_UUID = UUID.randomUUID()
val DEVICE_UUID_STRING = DEVICE_UUID.toString()

//region Reddit

const val REDDIT_DEV_USERNAME = "notrobots-dev"
val REDDIT_USER_AGENT = "android:${App.PACKAGE_NAME}:${App.VERSION} (by /u/$REDDIT_DEV_USERNAME)"

//endregion

//region Twitter

const val TWITTER_DEV_USERNAME = "NotRobotsDev"
val TWITTER_USER_AGENT = "android:${App.PACKAGE_NAME}:${App.VERSION} (by @$TWITTER_DEV_USERNAME)"

//endregion

//region Tumblr

const val TUMBLR_DEV_USERNAME = "notrobotsdev"
val TUMBLR_USER_AGENT = "android:${App.PACKAGE_NAME} (by $TUMBLR_DEV_USERNAME.tumblr.com)"

// TODO These needs to be stored in the environment variables for security reasons
val TUMBLR_CONSUMER_KEY = "TUMBLR_CONSUMER_KEY"
val TUMBLR_CONSUMER_SECRET = "TUMBLR_CONSUMER_SECRET"
val TUMBLR_REDIRECT_URI = "https://localhost/"

//endregion