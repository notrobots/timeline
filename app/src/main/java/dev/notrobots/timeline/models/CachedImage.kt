package dev.notrobots.timeline.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.Serializable

@Entity(primaryKeys = ["image_url", "image_postId"])
data class CachedImage(
    @ColumnInfo("image_url")
    var url: String,
    @ColumnInfo("image_postId")
    var postId: Long
): Serializable