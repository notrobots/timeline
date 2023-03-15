package dev.notrobots.timeline.oauth

import dev.notrobots.timeline.data.*

class TumblrApi20 private constructor(
    clientId: String,
    clientSecret: String?,
    userAgent: String,
    state: String,
    scope: String,
    callback: String?
) : Api20(clientId, clientSecret, userAgent, state, scope, callback) {
    override fun getAccessTokenEndpoint(): String {
        return "https://api.tumblr.com/v2/oauth2/token"
    }

    override fun getAuthorizationBaseUrl(): String {
        return "https://www.tumblr.com/oauth2/authorize"
    }

    companion object {
        val INSTANCE = TumblrApi20(
            TUMBLR_CONSUMER_KEY,
            TUMBLR_CONSUMER_SECRET,
            TUMBLR_USER_AGENT,
            DEVICE_UUID_STRING,
            "basic offline_access",
            TUMBLR_REDIRECT_URI
        )
    }
}