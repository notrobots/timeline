package dev.notrobots.timeline.models.social

import androidx.room.Embedded
import androidx.room.Relation
import dev.notrobots.timeline.models.CachedImage
import dev.notrobots.timeline.models.Profile

abstract class PostWithMedia<P : Post>(
    @Embedded
    val post: P,
    @Relation(
        parentColumn = "postId",
        entityColumn = "postId"
    )
    val images: List<CachedImage>,
    @Relation(
        parentColumn = "profileId",
        entityColumn = "profileId"
    )
    var profile: Profile?
)