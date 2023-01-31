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
import dev.notrobots.timeline.models.social.SocialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimelineViewModel @Inject constructor(
    val redditRepository: RedditRepository,
    val redditPostDao: RedditPostDao,
    val cachedImageDao: CachedImageDao,
    val profileDao: ProfileDao
) : ViewModel() {
    private val socialRepositories: List<SocialRepository<*>> = listOf(
        redditRepository
    )
    private val socialRepositoriesProfilesObservers = socialRepositories.associateWith { repository ->
        Observer<List<Profile>> {
            viewModelScope.launch(Dispatchers.Default) {
                repository.refreshPosts()
            }
        }
    }
    private val socialRepositoriesPosts = socialRepositories.map { it.posts }
    val socialFilter = MutableLiveData<SocialFilter>()
    val logger = Logger(this)
    val posts = PostMediatorLiveData(socialRepositoriesPosts).map {
        it.sortedByDescending { it.post.timestamp }
    }

    init {
        registerProfilesObservers()
    }

    override fun onCleared() {
        super.onCleared()
        unregisterProfilesObservers()
    }

    fun setSocialFilter(socialFilter: SocialFilter) {
        this.socialFilter.value = socialFilter
    }

    fun refreshPosts() {
        //TODO: This should also check which socials are enabled and which are not
        viewModelScope.launch(Dispatchers.Default) {
            redditRepository.refreshPosts()
        }
    }

    private fun registerProfilesObservers() {
        for (repository in socialRepositories) {
            socialRepositoriesProfilesObservers[repository]?.let {
                repository.profiles.observeForever(it)
            }
        }
    }

    private fun unregisterProfilesObservers() {
        for (repository in socialRepositories) {
            socialRepositoriesProfilesObservers[repository]?.let {
                repository.profiles.removeObserver(it)
            }
        }
    }

    //TODO liveDataList type
    // liveDataList can accept any type but it must be LiveData<List<T>> where T is a child class of Post
    // this should be enforced in the future to avoid confusion and any issue
    class PostMediatorLiveData(liveDataList: List<LiveData<*>?>) : MediatorLiveData<List<PostWithMedia<*>>>() {
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