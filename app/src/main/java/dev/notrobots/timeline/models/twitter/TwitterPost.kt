package dev.notrobots.timeline.models.twitter

import androidx.room.Entity
import dev.notrobots.timeline.models.Socials
import dev.notrobots.timeline.models.social.Post

@Entity
class TwitterPost(
    timestamp: Long,
    profileId: Long
) : Post(timestamp, profileId, Socials.Twitter)