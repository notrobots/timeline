package dev.notrobots.timeline.models.tumblr

import com.tumblr.jumblr.types.Post
import dev.notrobots.timeline.models.Profile

object TumblrUtil {
    fun tumblrPost(post: Post, profile: Profile): TumblrPost {
        return TumblrPost(
            post.blogName,
            post.timestamp,
            profile.profileId
        )
    }
}