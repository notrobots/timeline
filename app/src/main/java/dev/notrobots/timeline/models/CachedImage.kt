package dev.notrobots.timeline.models

import androidx.room.Entity
import java.io.Serializable

@Entity(primaryKeys = ["url", "postId"])
data class CachedImage(
    var url: String,
    var postId: Long
): Serializable