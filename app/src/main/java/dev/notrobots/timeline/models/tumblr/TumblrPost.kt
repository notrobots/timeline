package dev.notrobots.timeline.models.tumblr

import androidx.room.Entity
import dev.notrobots.timeline.models.Socials
import dev.notrobots.timeline.models.social.Post

@Entity
class TumblrPost(
    blogName: String,
    timestamp: Long,
    profileId: Long
) : Post(timestamp, profileId, Socials.Tumblr)