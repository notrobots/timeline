package dev.notrobots.timeline.db

import androidx.room.Dao
import androidx.room.Query
import dev.notrobots.timeline.models.CachedImage
import dev.notrobots.timeline.models.Video

@Dao
interface VideoDao : BaseDao<Video> {
    @Query("SELECT * FROM Video WHERE video_postId = :postId")
    fun getVideos(postId: Long): List<Video>
}