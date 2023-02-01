package dev.notrobots.timeline.models.twitter

import dev.notrobots.timeline.models.Profile
import io.github.redouane59.twitter.dto.tweet.TweetType
import io.github.redouane59.twitter.dto.tweet.TweetV2
import java.text.SimpleDateFormat
import java.time.ZoneOffset

object TwitterUtil {
    fun twitterPost(tweet: TweetV2, profile: Profile): TwitterPost {
        return TwitterPost(
            tweet.createdAt.toEpochSecond(ZoneOffset.UTC),   //xxx Is this the right timezone?
            profile.profileId
        )
    }
}