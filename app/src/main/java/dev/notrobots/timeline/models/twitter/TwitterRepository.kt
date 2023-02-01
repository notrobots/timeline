package dev.notrobots.timeline.models.twitter

import androidx.lifecycle.LiveData
import dev.notrobots.timeline.db.ProfileDao
import dev.notrobots.timeline.models.Profile
import dev.notrobots.timeline.models.Socials
import dev.notrobots.timeline.models.social.SocialRepository
import io.github.redouane59.twitter.TwitterClient
import javax.inject.Inject

class TwitterRepository @Inject constructor(
    val twitterPostDao: TwitterPostDao,
    val profileDao: ProfileDao
) : SocialRepository<List<TwitterPostWithMedia>>() {
    override val posts = twitterPostDao.getPostsWithMediaLive()
    override val profiles = profileDao.getProfilesLive(Socials.Twitter)

    override suspend fun refreshPosts(profile: Profile) {


        TODO("Not yet implemented")
    }
}