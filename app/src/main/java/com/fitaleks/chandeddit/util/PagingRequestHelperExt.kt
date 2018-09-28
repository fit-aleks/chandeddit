package com.fitaleks.chandeddit.util

import android.arch.paging.PagingRequestHelper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitaleks.chandeddit.repository.NetworkState

/**
 * Created by Alexander on 04.01.2018.
 */
private fun getErrorMessage(report: PagingRequestHelper.StatusReport): String {
    return PagingRequestHelper.RequestType.values().mapNotNull {
        report.getErrorFor(it)?.message
    }.first()
}

fun PagingRequestHelper.createStatusLiveData(): LiveData<NetworkState> {
    val liveData = MutableLiveData<NetworkState>()
    addListener { report ->
        when {
            report.hasRunning() -> liveData.postValue(NetworkState.LOADING)
            report.hasError() -> liveData.postValue(
                    NetworkState.error(getErrorMessage(report)))
            else -> liveData.postValue(NetworkState.LOADED)
        }
    }
    return liveData
}