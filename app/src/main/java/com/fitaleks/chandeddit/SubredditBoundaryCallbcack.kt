package com.fitaleks.chandeddit

import android.arch.paging.PagedList
import android.arch.paging.PagingRequestHelper
import com.fitaleks.chandeddit.api.RedditApi
import com.fitaleks.chandeddit.data.RedditPost
import com.fitaleks.chandeddit.util.createStatusLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

/**
 * Created by Alexander on 05.01.2018.
 */
class SubredditBoundaryCallbcack(private val subredditName: String,
                                 private val webService: RedditApi,
                                 private val handleResponse: (String, RedditApi.ListingResponse?) -> Unit,
                                 private val ioExecutor: Executor,
                                 private val pageSize: Int) : PagedList.BoundaryCallback<RedditPost>() {

    val helper = PagingRequestHelper(ioExecutor)
    val networkState = helper.createStatusLiveData()

    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            webService.getTop(pageSize).enqueue(createCallback(it))
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: RedditPost) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            webService.getTopAfter(itemAtEnd.name, pageSize).enqueue(createCallback(it))
        }
    }

    private fun insertItemsIntoDb(pagingCallback: PagingRequestHelper.Request.Callback, response: Response<RedditApi.ListingResponse>) {
        ioExecutor.execute {
            pagingCallback.recordSuccess()
            handleResponse.invoke(subredditName, response.body())
        }
    }

    private fun createCallback(pagingCallback: PagingRequestHelper.Request.Callback): Callback<RedditApi.ListingResponse> {
        return object : Callback<RedditApi.ListingResponse> {
            override fun onResponse(call: Call<RedditApi.ListingResponse>, response: Response<RedditApi.ListingResponse>) {
                insertItemsIntoDb(pagingCallback, response)
            }

            override fun onFailure(call: Call<RedditApi.ListingResponse>?, t: Throwable) {
                pagingCallback.recordFailure(t)
            }
        }
    }
}