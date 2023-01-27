package dev.notrobots.timeline.ui.timeline

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.notrobots.androidstuff.util.Logger
import dev.notrobots.timeline.db.CachedImageDao
import dev.notrobots.timeline.db.ProfileDao
import dev.notrobots.timeline.models.*
import dev.notrobots.timeline.models.social.PostWithMedia
import dev.notrobots.timeline.models.reddit.RedditPostDao
import dev.notrobots.timeline.models.reddit.RedditRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimelineViewModel @Inject constructor(
//    val fakeTwitterRepository: FakeTwitterRepository,
//    val fakeTwitterPostDao: FakeTwitterPostDao,
//    val fakeRedditRepository: FakeRedditRepository,
//    val fakeRedditPostDao: FakeRedditPostDao,
    val redditRepository: RedditRepository,
    val redditPostDao: RedditPostDao,
    val cachedImageDao: CachedImageDao,
    val profileDao: ProfileDao
) : ViewModel() {
    val socialFilter = MutableLiveData<SocialFilter>()
//    val fakeTwitterPosts = fakeTwitterRepository.posts
//    val fakeRedditPosts = fakeRedditRepository.posts
    val logger = Logger(this)
    val posts = PostMediatorLiveData(
        redditRepository.posts
    ).map {
        it.sortedByDescending { it.post.timestamp }
    }
    val redditProfileObserver = Observer<List<Profile>> {
        viewModelScope.launch(Dispatchers.Default) {
            redditRepository.refreshPosts()
        }
    }


//    val posts = socialFilter.switchMap {
//        fakeTwitterRepository.setEnabledProfiles(it.enabledProfiles[Social.FakeTwitter]!!)  //xxx Update these only when the enabled accounts change?
//        fakeRedditRepository.setEnabledProfiles(it.enabledProfiles[Social.FakeReddit]!!)

//        PostMediatorLiveData(
//            if (it.fakeTwitter) fakeTwitterPosts else null,
//            if (it.fakeReddit) fakeRedditPosts else null,
//        )
//    }.map {
//        it.sortedByDescending { it.post.timestamp }
//    }
//    val fakeRedditProfilesObserver = Observer<List<Profile>> {
//        viewModelScope.launch(Dispatchers.Default) {
//            fakeRedditRepository.refreshPosts()
//        }
//    }
//    val fakeTwitterProfilesObserver = Observer<List<Profile>> {
//        viewModelScope.launch(Dispatchers.Default) {
//            fakeTwitterRepository.refreshPosts()
//        }
//    }

    init {
        redditRepository.profiles.observeForever(redditProfileObserver)

//        fakeRedditRepository.profiles.observeForever(fakeRedditProfilesObserver)
//        fakeRedditRepository.enabledProfiles.observeForever(fakeRedditProfilesObserver)
//        fakeTwitterRepository.profiles.observeForever(fakeTwitterProfilesObserver)
//        fakeTwitterRepository.enabledProfiles.observeForever(fakeTwitterProfilesObserver)
    }

    override fun onCleared() {
        super.onCleared()
        redditRepository.profiles.removeObserver(redditProfileObserver)

//        fakeRedditRepository.profiles.removeObserver(fakeRedditProfilesObserver)
//        fakeRedditRepository.enabledProfiles.removeObserver(fakeRedditProfilesObserver)
//        fakeTwitterRepository.profiles.removeObserver(fakeTwitterProfilesObserver)
//        fakeTwitterRepository.enabledProfiles.removeObserver(fakeTwitterProfilesObserver)
    }

//    val posts = PostMediatorLiveData(
//        fakeTwitterPosts,
//        fakeRedditPosts
//    ).map {
//        it.sortedByDescending { it.post.timestamp }
//    }

    fun setSocialFilter(socialFilter: SocialFilter) {
        this.socialFilter.value = socialFilter
    }

    fun refreshPosts() {
        //TODO: This should also check which socials are enabled and which are not
        viewModelScope.launch(Dispatchers.Default) {
            redditRepository.refreshPosts()
//            fakeTwitterRepository.refreshPosts()
//            fakeRedditRepository.refreshPosts()
        }
    }

//    fun debugUpdateRandomPost() {
//        viewModelScope.launch(Dispatchers.Default) {
//            val posts = fakeTwitterPostDao.getPosts()
//
//            if (posts.isNotEmpty()) {
//                val post = Random.nextItem(posts)
//
//                post.text = Random.nextString(12, 25, TestUtil.ALPHABET)
//                post.timestamp = now()
//
//                fakeTwitterPostDao.update(post)
//                logger.logi("Updated post with id: ${post.postId}")
//            }
//        }
//    }
//
//    fun debugDeleteRandomPost() {
//        viewModelScope.launch(Dispatchers.Default) {
//            val posts = fakeTwitterPostDao.getPosts()
//
//            if (posts.isNotEmpty()) {
//                val post = Random.nextItem(fakeTwitterPostDao.getPosts())
//
//                fakeTwitterPostDao.delete(post)
//                logger.logi("Deleted post with id: ${post.postId}")
//            }
//        }
//    }
//
//    fun debugAddRandomPost(context: Context) {
//        viewModelScope.launch(Dispatchers.Default) {
////            val cacheDir by lazy {
////                context.cacheDir.absolutePath
////            }
//            val timestamp = now()
//
//            when (Random.nextInt(2)) {
//                // FakeTwitter
//                0 -> {
//                    val post = TestUtil.randomFakeTwitterPost()
//
//                    post.timestamp = timestamp
//                    post.profileId = 1  //Taken from App.kt
//
//                    val postId = fakeTwitterPostDao.insert(post)
//                    val imageCount = Random.nextInt(0, 5)
//
//                    logger.logi("Added post with id: $postId")
//
//                    if (imageCount > 0) {
//                        for (i in 0 until imageCount) {
//                            val imageUrl = TestUtil.randomPostImage()
//
////                if (!cachedImageDao.exists(imageUrl)) {
////                    val imageBytes = Network.downloadImageSync(imageUrl)
////                    val cachedImagePath = cacheDir + File.separator + "twitter_temp_img_${now()}"
////                    val cachedImageUri = File(cachedImagePath).toUri()
////
////                    context.contentResolver.openOutputStream(cachedImageUri)?.use { stream ->
////                        stream.write(imageBytes)
////                        cachedImageDao.insert(
////                            CachedImage(
////                                imageUrl,
////                                postId,
////                            )
////                        )
////                    }
////
////                    logger.logi("New image added: $imageUrl")
////                }
//                            cachedImageDao.insert(
//                                CachedImage(
//                                    imageUrl,
//                                    postId,
//                                )
//                            )
//                        }
//                    }
//                }
//
//                // FakeReddit
//                1 -> {
//                    val post = TestUtil.randomFakeRedditPost()
//
//                    post.timestamp = timestamp
//                    post.profileId = 2  //Taken from App.kt
//
//                    fakeRedditPostDao.insert(post)
//                }
//            }
//        }
//    }

    //TODO liveDataList type
    // liveDataList can accept any type but it must be LiveData<List<T>> where T is a child class of Post
    // this should be enforced in the future to avoid confusion and any issue
    class PostMediatorLiveData(vararg liveDataList: LiveData<*>?) : MediatorLiveData<List<PostWithMedia<*>>>() {
        private val liveDataMap = mutableMapOf<Int, List<PostWithMedia<*>>?>()

        init {
            for (liveData in liveDataList.filterNotNull()) {
//                val newList = liveDataList.map { it.value as List<Post>? }.toMutableList()

                // This sets up an observer that notifies whenever the [liveData]'s value changes
                // The given value in the code block is the new value which should be of type List<Post>
                addSource(liveData) {
                    // This cast should never fail unless the PostMediatorLiveData
                    // is constructed with the wrong types
                    it as List<PostWithMedia<*>>

                    liveDataMap[liveData.hashCode()] = it
                    value = liveDataMap.values.filterNotNull().flatten()
                }
            }
        }
    }
}