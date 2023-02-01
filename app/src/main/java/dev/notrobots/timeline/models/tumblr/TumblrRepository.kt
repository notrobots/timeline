package dev.notrobots.timeline.models.tumblr

import androidx.lifecycle.LiveData
import dev.notrobots.timeline.db.ProfileDao
import dev.notrobots.timeline.models.Profile
import dev.notrobots.timeline.models.Socials
import dev.notrobots.timeline.models.social.SocialRepository
import javax.inject.Inject

class TumblrRepository @Inject constructor(
    tumblrPostDao: TumblrPostDao,
    profileDao: ProfileDao
) : SocialRepository<List<TumblrPostWithMedia>>() {
    override val posts = tumblrPostDao.getPostsWithMediaLive()
    override val profiles = profileDao.getProfilesLive(Socials.Tumblr)

    override suspend fun refreshPosts(profile: Profile) {
        TODO("Not yet implemented")
    }
}