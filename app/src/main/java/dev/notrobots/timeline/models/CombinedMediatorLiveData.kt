package dev.notrobots.timeline.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

open class CombinedMediatorLiveData<R>(
    liveData: List<LiveData<*>>,
    private val combine: (list: List<Any?>) -> R
) : MediatorLiveData<R>() {
    private val liveDataList: MutableList<Any?> = MutableList(liveData.size) { null }

    constructor(vararg liveData: LiveData<*>, combine: (list: List<Any?>) -> R) : this(liveData.toList(), combine)

    init {
        for (i in liveData.indices) {
            super.addSource(liveData[i]) {
                liveDataList[i] = it
                value = combine(liveDataList)
            }
        }
    }
}