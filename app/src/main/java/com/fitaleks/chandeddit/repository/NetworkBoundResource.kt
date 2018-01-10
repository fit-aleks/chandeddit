package com.fitaleks.chandeddit.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import com.fitaleks.chandeddit.data.Resource
import retrofit2.Response
import java.util.concurrent.Executor



/**
 *  * A generic class that can provide a resource backed by both the sqlite database and the network.
 * <p>
 * You can read more about it in the <a href="https://developer.android.com/arch">Architecture Guide</a>.
 * @param <ResultType>
 * @param <RequestType>
 *
 * Created by alex206512252 on 1/8/18.
 */
abstract class NetworkBoundResource<ResultType, RequestType>(private val mainThreadExecutor: Executor,
                                                             private val ioExecutor: Executor) {
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading()
        val dbSource = loadFromDb()
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData -> setValue(Resource.success(newData)) }
            }
        }
    }

    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()
        result.addSource(dbSource) { newData -> setValue(Resource.loading(newData)) }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            if (response?.isSuccessful == true) {
                ioExecutor.execute {
                    saveCallResult(processResponse(response))
                    mainThreadExecutor.execute {
                        result.addSource(loadFromDb()) { newData ->
                            setValue(Resource.success(newData))
                        }
                    }
                }
            } else {
                onFetchFailed()
                result.addSource(dbSource) { newData ->
                    setValue(Resource.errror(response?.errorBody()?.toString(), newData))
                }
            }
        }
    }

    protected fun onFetchFailed() {}

    fun asLiveData(): LiveData<Resource<ResultType>> = result

    protected fun processResponse(response: Response<RequestType>): RequestType? = response.body()

    protected abstract fun saveCallResult(item: RequestType?)
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @MainThread
    protected abstract fun createCall(): LiveData<Response<RequestType>>
}