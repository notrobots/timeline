package dev.notrobots.timeline.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.notrobots.timeline.models.*
import dev.notrobots.timeline.models.reddit.RedditPost
import dev.notrobots.timeline.models.reddit.RedditPostDao

@Database(
    entities = [
        CachedImage::class,
        Video::class,
        Profile::class,
        RedditPost::class
    ],
    version = 2
)
abstract class TimelineDatabase : RoomDatabase() {
    abstract fun cachedImageDao(): CachedImageDao
    abstract fun profileDao(): ProfileDao
    abstract fun redditPostDao(): RedditPostDao
    abstract fun videoDao(): VideoDao
}