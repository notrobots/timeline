package dev.notrobots.timeline.models.social

import androidx.room.PrimaryKey
import dev.notrobots.timeline.util.hashCodeOf

abstract class Post(
    var timestamp: Long,

    /**
     * Id of the profile this post belongs to.
     */
    var profileId: Long = 0,

    /**
     * String identifier of the social.
     */
    var social: String
) {
    /**
     * Identifier for this entity in the local database.
     */
    @PrimaryKey(autoGenerate = true)
    var postId: Long = 0

    override fun equals(other: Any?): Boolean {
        return other is Post &&
               other.timestamp == timestamp &&
               other.social == social &&
               other.postId == postId &&
               other.profileId == profileId
    }

    override fun hashCode(): Int {
        return hashCodeOf(
            timestamp,
            social,
            postId,
            profileId
        )
    }


//    override fun hashCode(): Int {
//        return hashCodeOf(timestamp, social, postId, profileId)
//    }
}