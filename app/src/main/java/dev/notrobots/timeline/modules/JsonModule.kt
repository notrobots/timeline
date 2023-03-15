package dev.notrobots.timeline.modules

import com.github.scribejava.core.model.OAuth2AccessToken
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object JsonModule {
    @Provides
    @Singleton
    fun moshi(): Moshi {
        return Moshi.Builder()
            .build()
    }

//    @Provides
//    @Singleton
//    fun oAuthToken(): JsonAdapter<OAuth2AccessToken> {
//        return oAuthTokenAdapter
//    }
}