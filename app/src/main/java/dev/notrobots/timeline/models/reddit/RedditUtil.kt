package dev.notrobots.timeline.models.reddit

import dev.notrobots.androidstuff.util.Logger
import dev.notrobots.timeline.data.REDDIT_USER_AGENT
import dev.notrobots.timeline.models.Profile
import net.dean.jraw.models.Submission
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

object RedditUtil {
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

    fun getVideoEmbeds(submission: Submission): List<String> {
        if (submission.isSelfPost) {
            return emptyList()
        }

        submission.embeddedMedia?.let {
            it.oEmbed?.let {
                if (it.type == "video") {
                    it.embedHtml?.let {
                        return listOf(it)
                    }
                }
            }
        }

        return emptyList()
    }

    fun getVideoUrls(submission: Submission): List<String> {
        if (submission.isSelfPost) {
            return emptyList()
        }

        submission.embeddedMedia?.let {
            it.redditVideo?.let {
                return listOf(it.fallbackUrl)
            }
        }

        return emptyList()
    }

    //FIXME: If a post is a crosspost the images and videos won't show up in the crosspost
    // to fix this you need to check if the submission is a crosspost and then use this method
    // on the original post
    suspend fun getImageUrls(submission: Submission): List<String> {
        if (submission.isSelfPost) {
            return emptyList()
        }

        submission.embeddedMedia?.let {
            it.oEmbed?.let {
                if (it.type == "photo") {
                    return listOf(it.url!!)
                }
            }
        }

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