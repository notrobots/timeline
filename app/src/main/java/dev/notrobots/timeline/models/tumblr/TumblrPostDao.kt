package dev.notrobots.timeline.models.tumblr

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import dev.notrobots.timeline.db.BaseDao
import dev.notrobots.timeline.models.twitter.TwitterPost
import dev.notrobots.timeline.models.twitter.TwitterPostWithMedia

@Dao
interface TumblrPostDao : BaseDao<TumblrPost> {
    @Query("SELECT * FROM TwitterPost")
    fun getPosts(): List<TumblrPost>

    @Query("SELECT * FROM TwitterPost")
    fun getPostsLive(): LiveData<List<TumblrPost>>

    @Query(GET_POSTS_WITH_MEDIA)
    fun getPostsWithMedia(): List<TumblrPostWithMedia>

    @Query(GET_POSTS_WITH_MEDIA)
    fun getPostsWithMediaLive(): LiveData<List<TumblrPostWithMedia>>

    companion object {
        private const val GET_POSTS_WITH_MEDIA =
            """
                --SELECT * FROM (
                    SELECT * FROM TumblrPost
                    
                    -- Post images
                    LEFT JOIN CachedImage
                    ON TumblrPost.postId = CachedImage.image_postId
                    
                    -- Post videos
                    LEFT JOIN Video
                    ON TumblrPost.postId = Video.video_postId
                    
                    -- Profile info
                    INNER JOIN Profile
                    ON TumblrPost.profileId = Profile.profileId 
                        -- AND TumblrPost.profileId in (:enabledProfiles)
                    
                    GROUP BY TumblrPost.postId
                --)
                --WHERE TumblrPost.profileId in (:enabledProfiles)
            """
    }
}