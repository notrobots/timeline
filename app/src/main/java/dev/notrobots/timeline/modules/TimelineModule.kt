package dev.notrobots.timeline.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TimelineModule {
    @Provides
    @Singleton
    fun okHttpClient(): OkHttpClient {
        return OkHttpClient()
    }
}