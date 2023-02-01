package dev.notrobots.timeline.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.notrobots.timeline.models.*
import dev.notrobots.timeline.models.reddit.RedditPost
import dev.notrobots.timeline.models.reddit.RedditPostDao
import dev.notrobots.timeline.models.tumblr.TumblrPost
import dev.notrobots.timeline.models.tumblr.TumblrPostDao
import dev.notrobots.timeline.models.twitter.TwitterPost
import dev.notrobots.timeline.models.twitter.TwitterPostDao

@Database(
    entities = [
        CachedImage::class,
        Video::class,
        Profile::class,
        RedditPost::class,
        TwitterPost::class,
        TumblrPost::class
    ],
    version = 2
)
abstract class TimelineDatabase : RoomDatabase() {
    abstract fun cachedImageDao(): CachedImageDao
    abstract fun profileDao(): ProfileDao
    abstract fun redditPostDao(): RedditPostDao
    abstract fun twitterPostDao(): TwitterPostDao
    abstract fun tumblrPostDao(): TumblrPostDao
    abstract fun videoDao(): VideoDao
}