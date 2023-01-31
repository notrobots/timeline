package dev.notrobots.timeline.models.reddit

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import dev.notrobots.timeline.db.BaseDao

@Dao
interface RedditPostDao : BaseDao<RedditPost> {
    @Query("SELECT * FROM RedditPost")
    suspend fun getPosts(): List<RedditPost>

    @Query("SELECT * FROM RedditPost")
    fun getPostsLive(): LiveData<List<RedditPost>>

    @Query(GET_POSTS_WITH_MEDIA)
    suspend fun getPostsWithMedia(): List<RedditPostWithMedia>

    @Query(GET_POSTS_WITH_MEDIA)
    fun getPostsWithMediaLive(): LiveData<List<RedditPostWithMedia>>

    @Query("SELECT * FROM RedditPost WHERE remotePostId = :remoteId")
    suspend fun findPostWithRemoteId(remoteId: String): RedditPost?

    companion object {
        const val GET_POSTS_WITH_MEDIA =
            """
                --SELECT * FROM (
                    SELECT * FROM RedditPost
                    
                    -- Post images
                    LEFT JOIN CachedImage
                    ON RedditPost.postId = CachedImage.image_postId
                    
                    -- Post videos
                    LEFT JOIN Video
                    ON RedditPost.postId = Video.video_postId
                    
                    -- Profile info
                    INNER JOIN Profile
                    ON RedditPost.profileId = Profile.profileId 
                        -- AND RedditPost.profileId in (:enabledProfiles)
                    
                    GROUP BY RedditPost.postId
                --)
                --WHERE RedditPost.profileId in (:enabledProfiles)
            """
    }
}