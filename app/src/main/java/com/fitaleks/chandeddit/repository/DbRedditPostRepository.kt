package com.fitaleks.chandeddit.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import com.fitaleks.chandeddit.SubredditBoundaryCallbcack
import com.fitaleks.chandeddit.api.RedditApi
import com.fitaleks.chandeddit.data.RedditPost
import com.fitaleks.chandeddit.data.Resource
import com.fitaleks.chandeddit.db.RedditDb
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

/**
 * Created by alex206512252 on 1/2/18.
 */
class DbRedditPostRepository(val db: RedditDb,
                             private val redditApi: RedditApi,
                             private val mainThreadExecutor: Executor,
                             private val ioExecutor: Executor,
                             private val networkPageSize: Int = DEFAULT_PAGE_SIZE) : RedditPostRepository {
    companion object {
        private const val DEFAULT_PAGE_SIZE = 10
    }

    private fun insertResultIntoDb(subredditName: String, response: RedditApi.ListingResponse?) {
        response?.let {
            db.runInTransaction {
                val start = db.posts().getNextIndexInSubreddit(subredditName)
                val items = it.data.children.mapIndexed { index, child ->
                    child.data.indexInResponse = start + index
                    child.data
                }
                db.posts().insert(items)
            }

        }
    }

    private fun refresh(subredditName: String): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        redditApi.getTop(networkPageSize).enqueue(object : Callback<RedditApi.ListingResponse> {
            override fun onResponse(call: Call<RedditApi.ListingResponse>, response: Response<RedditApi.ListingResponse>) {
                ioExecutor.execute {
                    db.runInTransaction {
                        db.posts().deleteySubreddit(subredditName)
                        insertResultIntoDb(subredditName, response = response.body())
                    }
                    networkState.postValue(NetworkState.LOADED)
                }
            }

            override fun onFailure(call: Call<RedditApi.ListingResponse>?, t: Throwable) {
                networkState.value = NetworkState.error(t.message)
            }
        })
        return networkState
    }

    override fun postsOfSubreddit(subreddit: String, itemsPerPage: Int): Listing<RedditPost> {
        val boundaryCallback = SubredditBoundaryCallbcack(subreddit,
                redditApi,
                this::insertResultIntoDb,
                ioExecutor,
                DEFAULT_PAGE_SIZE)
        val dataSourceFactory = db.posts().postsBySubreddit(subreddit)
        val listBuilder = LivePagedListBuilder<Int, RedditPost>(dataSourceFactory, itemsPerPage)
                .setBoundaryCallback(boundaryCallback)

        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger, {
            refresh(subreddit)
        })

        return Listing(
                pagedList = listBuilder.build(),
                networkState = boundaryCallback.networkState,
                retry = {
                    boundaryCallback.helper.retryAllFailed()
                },
                refresh = {
                    refreshTrigger.value = null
                },
                refreshState = refreshState
        )
    }

    override fun postById(postId: String): LiveData<Resource<RedditPost>> {
        return object : NetworkBoundResource<RedditPost, RedditApi.ListingResponse>(mainThreadExecutor, ioExecutor) {
            override fun saveCallResult(item: RedditApi.ListingResponse?) {
                if (item?.data?.children?.isNotEmpty() == true) {
                    insertResultIntoDb(item.data.children[0].data.subreddit, item)
                }
            }

            override fun shouldFetch(data: RedditPost?): Boolean = data == null

            override fun loadFromDb(): LiveData<RedditPost> = db.posts().getOnePostById(postId)

            override fun createCall(): LiveData<Response<RedditApi.ListingResponse>> {
                return MutableLiveData<Response<RedditApi.ListingResponse>>().apply {
                    value = redditApi.getPostsById(postId).value
                }
            }
        }.asLiveData()
    }
}