package dev.notrobots.timeline.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import dev.notrobots.timeline.models.CachedImage

@Dao
interface CachedImageDao {
    @Query("SELECT * FROM CachedImage WHERE postId = :postId")
    fun getCachedImages(postId: Long): List<CachedImage>

    @Insert(onConflict = IGNORE)
    suspend fun insert(cachedImage: CachedImage): Long
}