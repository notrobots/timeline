package dev.notrobots.timeline.models.reddit

import dev.notrobots.timeline.models.CachedImage
import dev.notrobots.timeline.db.CachedImageDao
import dev.notrobots.timeline.models.Profile
import dev.notrobots.timeline.db.ProfileDao
import dev.notrobots.timeline.models.Socials
import dev.notrobots.timeline.models.social.SocialRepository
import dev.notrobots.timeline.util.SocialManager
import net.dean.jraw.models.Submission
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.pagination.DefaultPaginator
import javax.inject.Inject

//todo: RedditPostRepository
class RedditRepository @Inject constructor(
    val redditPostDao: RedditPostDao,
    val profileDao: ProfileDao,
    val cachedImageDao: CachedImageDao
) : SocialRepository<List<RedditPostWithMedia>>() {
    override val posts = redditPostDao.getPostsWithMediaLive()
    override val profiles = profileDao.getProfilesLive(Socials.Reddit)

    private var paginator: DefaultPaginator<Submission>? = null

    override suspend fun refreshPosts(profile: Profile) {
        val tokenStore = SocialManager.redditTokenStore

        if (tokenStore.size() > 0) {
            val userIndex = tokenStore.usernames.indexOf(profile.username)

            // Each AccountHelper can only keep one active account at a time
            // so to have multiple reddit profiles active at a time either
            // you will need to have multiple AccountHelpers, possibly linked to an username
            // In that way each AccountHelper will have 1 single account.
            // What happens with the TokenStore??
            // If having multiple AccountHelpers doesn't work, you will need to switch account
            // every time you need to fetch some posts which might have an high cost

            if (userIndex < 0) {
                logger.loge("Local user '${profile.username}' is not present in the TokenStore")
                return
            }

            SocialManager.redditAuthenticateIfNeeded(profile)

            val redditClient = SocialManager.redditAccountHelper[profile]?.reddit

            if (redditClient == null) {
                logger.loge("Refresh token not found or unexpired OAuthData")
                return
            }

//            if (paginator == null) {
                paginator = redditClient.frontPage()
                    .sorting(SubredditSort.NEW)
                    .build()
//            }

            // Browsing by "new" isn't the default on reddit, unlike on twitter.
            // If you browse by new this method should fetch the most recent posts and if you go down
            // it should look for older posts and add them at the bottom
            // If you refresh it should just refresh the current "first" page and restart from the first page so the
            // paginator should be reset

            val listing = paginator!!.next()
//            val posts = listing.children.map {
//                RedditPostConverter.redditPost(it, profile)
//            }

            logger.logi("Refreshing posts from profile $profile")

            for (submission in listing.children) {  //FIXME: These needs to be added all together because of how the livedata observer works
                val post = RedditPostConverter.redditPost(submission, profile)
                val storedPost = redditPostDao.findPostWithRemoteId(post.remotePostId)
                val images = RedditPostConverter.getImageUrls(submission)
                var postId = storedPost?.postId ?: 0

                if (storedPost == null) {
                    postId = redditPostDao.insert(post)
                } else {
                    redditPostDao.update(post)
                }

                images.forEach {
                    val cachedImage = CachedImage(it, postId)

                    cachedImageDao.insert(cachedImage)
                }
            }

//            logger.logi("Found ${posts.size} posts")

//            redditPostDao.insertOrUpdate(posts)

            // If you decide to use one single AccountHelper then you should probably logout after each
            // profile uses the helper
        } else {
            logger.loge("TokenStore is empty")
        }
    }
}