package dev.notrobots.timeline.models.tumblr

import dev.notrobots.timeline.models.CachedImage
import dev.notrobots.timeline.models.Profile
import dev.notrobots.timeline.models.social.PostWithMedia

class TumblrPostWithMedia(
    post: TumblrPost,
    images: List<CachedImage>,
    profile: Profile?
) : PostWithMedia<TumblrPost>(post, images, profile)