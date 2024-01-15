package dev.notrobots.timeline.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.notrobots.timeline.util.hashCodeOf
import java.io.Serializable

@Entity
data class Profile(
    val username: String,
    val social: String,
    val enabled: Boolean = true,
    val clientId: String
): Serializable {
    @PrimaryKey(autoGenerate = true)
    var profileId: Long = 0L

    override fun equals(other: Any?): Boolean {
        return other is Profile &&
               other.username == username &&
               other.social == social &&
               other.enabled == enabled &&
               other.profileId == profileId &&
               other.clientId == clientId
    }

    override fun hashCode(): Int {
        return hashCodeOf(
            username,
            social,
            enabled,
            profileId,
            clientId
        )
    }

    override fun toString(): String {
        return username + "_" + social
    }
}