package dev.notrobots.timeline.models.reddit

import androidx.room.Entity
import dev.notrobots.timeline.models.Socials
import dev.notrobots.timeline.models.social.Post

@Entity
class RedditPost(
    val author: String,
    val isSelfPost: Boolean,
    val selfText: String?,
    val url: String?,
    val remotePostId: String,
    timestamp: Long,
    profileId: Long
) : Post(timestamp, profileId, Socials.Reddit)