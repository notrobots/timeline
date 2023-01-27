package dev.notrobots.timeline.models.reddit

import dev.notrobots.androidstuff.util.Logger
import dev.notrobots.timeline.data.REDDIT_USER_AGENT
import dev.notrobots.timeline.models.Profile
import net.dean.jraw.models.Submission
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

object RedditPostConverter {
    private val logger = Logger(this)
    val REDDIT_GALLERY_URL = "^https?://(www|new|old).reddit.com/gallery/\\w+\$".toRegex(RegexOption.IGNORE_CASE)

    fun redditPost(submission: Submission, profile: Profile): RedditPost {
        return RedditPost(
            submission.author,
            submission.isSelfPost,
            submission.selfText,
            submission.url,
            submission.id,
            submission.created.time,
            profile.profileId
        )
    }

    suspend fun getImageUrls(submission: Submission): List<String> {
        if (submission.isSelfPost) {
            return emptyList()
        }

//        submission.embeddedMedia?.let {
//            it.redditVideo?.let {
//                // it.fallbackUrl   videos can't be saved to the database yet
//                logger.logw("Videos not supported")
//                return emptyList()
//            }
//
//            it.oEmbed?.let {
//                when (it.type) {
//                    "photo" -> return listOf(it.url!!)
//                     "video" ->  logger.logw("Videos not supported") //images.html can't be saved to the database yet
//                }
//            }
//        }

        if (submission.url.endsWith(".jpg") ||
            submission.url.endsWith(".png") ||
            submission.url.endsWith(".gif")
        ) {
            return listOf(submission.url)
        }

        if (submission.postHint == "image") {
            return listOf(submission.url)
        }

        if (submission.url.matches(REDDIT_GALLERY_URL)) {
            val postUrl = "https://www.reddit.com${submission.permalink}.json"

            return fetchRedditGallery(postUrl)
        }

        return emptyList()
    }

    private suspend fun fetchRedditGallery(url: String): List<String> {
        var conn: HttpURLConnection? = null
        val urls = mutableListOf<String>()

        try {
            conn = URL(url).openConnection() as HttpURLConnection
            conn.setRequestProperty("User-Agent", REDDIT_USER_AGENT)

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val content = conn.inputStream.bufferedReader().readText()
                val json = JSONArray(content)
                val mediaMetadata = json.getJSONObject(0)
                    .getJSONObject("data")
                    .getJSONArray("children")
                    .getJSONObject(0)
                    .getJSONObject("data")
                    .getJSONObject("media_metadata")

                for (mediaId in mediaMetadata.keys()) {
                    val media = mediaMetadata.getJSONObject(mediaId)

                    if (media.getString("e") == "Image") {
                        val source = media
                            .getJSONObject("s")
                            .getString("u")
                            .replace("&lt;", "<")
                            .replace("&gt;", ">")
                            .replace("&amp;", "&")

                        urls.add(source)
                    }
                }
            } else {
                logger.loge("Cannot fetch reddit gallery (${conn.responseCode})")
            }

            conn.disconnect()
        } catch (e: Exception) {
            logger.loge("Cannot fetch reddit gallery", e)
        } finally {
            conn?.disconnect()
        }

        return urls
    }
}