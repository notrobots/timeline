package dev.notrobots.timeline.models.reddit

import dev.notrobots.timeline.models.CachedImage
import dev.notrobots.timeline.models.Profile
import dev.notrobots.timeline.models.social.PostWithMedia
import net.dean.jraw.models.Submission

class RedditPostWithMedia(
    post: RedditPost,
    images: List<CachedImage>,
    profile: Profile
) : PostWithMedia<RedditPost>(post, images, profile)