package dev.notrobots.timeline.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.Serializable

@Entity(primaryKeys = ["video_url", "video_postId"])    //TODO: Pick different keys
data class Video(
    @ColumnInfo("video_postId")
    var postId: Long,
    @ColumnInfo("video_url")
    var url: String,
    @ColumnInfo("video_embed")
    var embed: String,
    @ColumnInfo("video_preview")
    val preview: String?
) : Serializable