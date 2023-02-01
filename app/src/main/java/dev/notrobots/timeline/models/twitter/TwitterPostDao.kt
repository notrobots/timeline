package dev.notrobots.timeline.models.twitter

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import dev.notrobots.timeline.db.BaseDao

@Dao
interface TwitterPostDao : BaseDao<TwitterPost> {
    @Query("SELECT * FROM TwitterPost")
    fun getPosts(): List<TwitterPost>

    @Query("SELECT * FROM TwitterPost")
    fun getPostsLive(): LiveData<List<TwitterPost>>

    @Query(GET_POSTS_WITH_MEDIA)
    fun getPostsWithMedia(): List<TwitterPostWithMedia>

    @Query(GET_POSTS_WITH_MEDIA)
    fun getPostsWithMediaLive(): LiveData<List<TwitterPostWithMedia>>

    companion object {
        private const val GET_POSTS_WITH_MEDIA =
            """
                --SELECT * FROM (
                    SELECT * FROM TwitterPost
                    
                    -- Post images
                    LEFT JOIN CachedImage
                    ON TwitterPost.postId = CachedImage.image_postId
                    
                    -- Post videos
                    LEFT JOIN Video
                    ON TwitterPost.postId = Video.video_postId
                    
                    -- Profile info
                    INNER JOIN Profile
                    ON TwitterPost.profileId = Profile.profileId 
                        -- AND TumblrPost.profileId in (:enabledProfiles)
                    
                    GROUP BY TwitterPost.postId
                --)
                --WHERE TumblrPost.profileId in (:enabledProfiles)
            """
    }
}