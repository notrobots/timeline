package dev.notrobots.timeline.models.twitter

import dev.notrobots.timeline.models.CachedImage
import dev.notrobots.timeline.models.Profile
import dev.notrobots.timeline.models.social.PostWithMedia

class TwitterPostWithMedia(
    post: TwitterPost,
    images: List<CachedImage>,
    profile: Profile?
) : PostWithMedia<TwitterPost>(post, images, profile)