package dev.notrobots.timeline.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.notrobots.timeline.models.*
import dev.notrobots.timeline.models.reddit.RedditPost
import dev.notrobots.timeline.models.reddit.RedditPostDao

@Database(
    entities = [
        CachedImage::class,
        Profile::class,
        RedditPost::class
    ],
    version = 1
)
abstract class TimelineDatabase : RoomDatabase() {
    abstract fun cachedImageDao(): CachedImageDao
    abstract fun profileDao(): ProfileDao
    abstract fun redditPostDao(): RedditPostDao
}