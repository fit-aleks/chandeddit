package com.fitaleks.chandeddit.repository

/**
 * Created by Alexander on 04.01.2018.
 */
enum class Status {
    RUNNING,
    SUCCESS,
    FAILED
}

data class NetworkState private constructor(
        val status: Status,
        val errorMsg: String? = null){
    companion object {
        val LOADED = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.RUNNING)
        fun error(msg: String? = null) = NetworkState(Status.FAILED, msg)
    }
}