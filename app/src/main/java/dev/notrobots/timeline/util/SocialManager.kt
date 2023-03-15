package dev.notrobots.timeline.util

import android.content.Context
import android.util.Log
import com.github.scribejava.core.model.OAuth2AccessToken
import dev.notrobots.libandroidstuff.extensions.toStringOrNull
import dev.notrobots.androidstuff.util.Logger
import dev.notrobots.timeline.data.*
import dev.notrobots.timeline.models.Profile
import dev.notrobots.timeline.models.Socials
import dev.notrobots.timeline.oauth.OAuth2AccessTokenStore
import dev.notrobots.timeline.oauth.SharedPreferencesTokenStore
import dev.notrobots.timeline.oauth.TumblrOAuthHelper
import net.dean.jraw.android.AndroidHelper
import net.dean.jraw.android.ManifestAppInfoProvider
import net.dean.jraw.android.SimpleAndroidLogAdapter
import net.dean.jraw.http.LogAdapter
import net.dean.jraw.http.SimpleHttpLogger
import net.dean.jraw.oauth.AccountHelper
import java.util.*

private typealias RedditTokenStore = net.dean.jraw.android.SharedPreferencesTokenStore

/**
 * Utility that handles login, authentication and logout
 */
object SocialManager {
    private val logger = Logger(this)
    var redditAccountHelper: MutableMap<Profile, AccountHelper> = mutableMapOf()
        private set
    var tumblrAccountHelpers = mutableMapOf<Profile, TumblrOAuthHelper>()
        private set
    lateinit var redditTokenStore: RedditTokenStore
        private set
    lateinit var genericTokenStore: dev.notrobots.timeline.oauth.SharedPreferencesTokenStore<OAuth2AccessToken>
        private set

    fun init(context: Context, profiles: List<Profile>) {
        genericTokenStore = OAuth2AccessTokenStore(context)
        genericTokenStore.load()
        genericTokenStore.autoPersist = true

        redditTokenStore = RedditTokenStore(context)
        redditTokenStore.load()
        redditTokenStore.autoPersist = true

        for (redditProfile in profiles.filterRedditProfiles()) {
            val accountHelper = redditAccountHelper(context)

            redditAccountHelper[redditProfile] = accountHelper
        }

        for (profile in profiles.filter { it.social == Socials.Tumblr }) {
//            val oAuthClient = TumblrOAuthHelper(
//                profile.toString(),
//                genericTokenStore,
//                TUMBLR_CONSUMER_KEY,
//                TUMBLR_CONSUMER_SECRET,
//                TUMBLR_USER_AGENT,
//                deviceUuid.toString(),
////                "www.tumblr.com/oauth2/authorize",
////                "api.tumblr.com/v2/oauth2/token",
//                "basic",
////                "https://localhost"
//            )

            tumblrAccountHelpers[profile] = tumblrHelper(profile)
        }
    }

    fun redditAccountHelper(context: Context): AccountHelper {
        val manifestAppInfoProvider = ManifestAppInfoProvider(context)
        val accountHelper = AndroidHelper.accountHelper(manifestAppInfoProvider, DEVICE_UUID, redditTokenStore)

        accountHelper.onSwitch { redditClient ->
            val logAdapter: LogAdapter = SimpleAndroidLogAdapter(Log.INFO)

            redditClient.logger = SimpleHttpLogger(SimpleHttpLogger.DEFAULT_LINE_LENGTH, logAdapter)
            logger.logi("Switched account to ${redditClient.me().username}")
        }

        return accountHelper
    }

    fun redditAddNewProfile(profile: Profile, accountHelper: AccountHelper) {
        if (profile.profileId == 0L) {
            logger.logw("Profile id is unset")
        }

        redditAccountHelper[profile] = accountHelper
    }

    fun redditAuthenticateIfNeeded(profile: Profile) {
        when (profile.social) {
            Socials.Reddit -> if (redditAccountHelper[profile]?.isAuthenticated() == false) {
                redditAccountHelper[profile]?.trySwitchToUser(profile.username)
            }
        }
    }

    fun redditLogout(profile: Profile) {
        when (profile.social) {
            Socials.Reddit -> {
                // FIXME Instead of switching to the account and logging it out
                //  this should have a map of Profiles and AccountHelpers
                //  so in that way the account helper has only 1 account
                //  and once it's logged out that instance of the account helper can be cleared

                val accountHelper = redditAccountHelper[profile]

                redditAuthenticateIfNeeded(profile)

                if (accountHelper?.trySwitchToUser(profile.username) != null) {
                    accountHelper.logout()
                }

                redditTokenStore.deleteLatest(profile.username)
                redditTokenStore.deleteRefreshToken(profile.username)
                redditAccountHelper.remove(profile)
            }
        }
    }

    fun tumblrHelper(profile: Profile? = null): TumblrOAuthHelper {
        return TumblrOAuthHelper(
            profile.toStringOrNull(),
            genericTokenStore
        )
    }

    fun twitterAddNewProfile(profile: Profile) {

    }

    fun tumblrAddNewProfile(profile: Profile) {

    }
}

private fun List<Profile>.filterRedditProfiles() = filter { it.social == Socials.Reddit }