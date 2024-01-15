package dev.notrobots.timeline.models.tumblr

import androidx.lifecycle.LiveData
import dev.notrobots.timeline.db.ProfileDao
import dev.notrobots.timeline.models.Profile
import dev.notrobots.timeline.models.Socials
import dev.notrobots.timeline.models.social.SocialRepository
import dev.notrobots.timeline.util.SocialManager
import javax.inject.Inject

class TumblrRepository @Inject constructor(
    val tumblrPostDao: TumblrPostDao,
    val profileDao: ProfileDao
) : SocialRepository<List<TumblrPostWithMedia>>() {
    override val posts = tumblrPostDao.getPostsWithMediaLive()
    override val profiles = profileDao.getProfilesLive(Socials.Tumblr)

    override suspend fun refreshPosts(profile: Profile) {
        val client = SocialManager.tumblrClients[profile]?.client

        client?.let { client ->
            for (remotePost in client.userDashboard()) {
                val post = TumblrUtil.tumblrPost(remotePost, profile)

                tumblrPostDao.insertOrUpdate(post)
            }
        }
    }
}