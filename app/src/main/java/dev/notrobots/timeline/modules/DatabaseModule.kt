package dev.notrobots.timeline.modules

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.notrobots.timeline.db.CachedImageDao
import dev.notrobots.timeline.db.ProfileDao
import dev.notrobots.timeline.db.TimelineDatabase
import dev.notrobots.timeline.db.VideoDao
import dev.notrobots.timeline.models.*
import dev.notrobots.timeline.models.reddit.RedditPostDao
import dev.notrobots.timeline.models.tumblr.TumblrPostDao
import dev.notrobots.timeline.models.twitter.TwitterPostDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    fun provideCachedImageDao(database: TimelineDatabase): CachedImageDao {
        return database.cachedImageDao()
    }

    @Provides
    fun profileDao(database: TimelineDatabase): ProfileDao {
        return database.profileDao()
    }

    @Provides
    fun redditPostDao(database: TimelineDatabase): RedditPostDao {
        return database.redditPostDao()
    }

    @Provides
    fun twitterPostDao(database: TimelineDatabase): TwitterPostDao {
        return database.twitterPostDao()
    }

    @Provides
    fun tumblrPostDao(database: TimelineDatabase): TumblrPostDao {
        return database.tumblrPostDao()
    }

    @Provides
    fun videoDao(database: TimelineDatabase): VideoDao {
        return database.videoDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TimelineDatabase {
        return Room.databaseBuilder(
            context,
            TimelineDatabase::class.java,
            "Timeline"
        ).build()
    }
}