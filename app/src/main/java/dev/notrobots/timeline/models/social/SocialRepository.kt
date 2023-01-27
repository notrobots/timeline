package dev.notrobots.timeline.models.social

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.notrobots.androidstuff.util.Logger
import dev.notrobots.timeline.models.Profile

abstract class SocialRepository<T> {
    protected val logger = Logger(this)
    abstract val posts: LiveData<T> //fixme LiveData<List<T>>
    abstract val profiles: LiveData<List<Profile>>
    val enabledProfiles = MutableLiveData(mutableListOf<Profile>())

    protected abstract suspend fun refreshPosts(profile: Profile)

    suspend fun refreshPosts() {
        profiles.value?.forEach {
            refreshPosts(it)
        }
    }

    fun setEnabledProfiles(profiles: List<Profile>) {
        enabledProfiles.value?.clear()
        enabledProfiles.value?.addAll(profiles)
    }
}