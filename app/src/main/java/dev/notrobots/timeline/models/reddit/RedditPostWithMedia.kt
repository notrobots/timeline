package dev.notrobots.timeline.models.reddit

import androidx.room.Relation
import dev.notrobots.timeline.models.CachedImage
import dev.notrobots.timeline.models.Profile
import dev.notrobots.timeline.models.Video
import dev.notrobots.timeline.models.social.PostWithMedia
import net.dean.jraw.models.Submission

class RedditPostWithMedia(
    post: RedditPost,
    images: List<CachedImage>,
    profile: Profile,
    @Relation(
        parentColumn = "postId",
        entityColumn = "video_postId"
    )
    val videos: List<Video>
) : PostWithMedia<RedditPost>(post, images, profile)