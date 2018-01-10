package com.fitaleks.chandeddit.data

import com.fitaleks.chandeddit.repository.NetworkState

/**
 * Generic class that holds a value with its loading status.
 *
 * Created by alex206512252 on 1/8/18.
 */
data class Resource<out T>(val status: NetworkState,
                           val data: T?) {
    companion object {
        fun <T> success(data: T?): Resource<T> = Resource(NetworkState.LOADED, data)
        fun <T> errror(errorMsg: String?, data: T?): Resource<T> = Resource(NetworkState.error(errorMsg), data)
        fun <T> loading(data: T? = null): Resource<T> = Resource(NetworkState.LOADING, data)
    }
}