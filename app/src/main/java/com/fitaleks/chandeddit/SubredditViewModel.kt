package com.fitaleks.chandeddit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import com.fitaleks.chandeddit.repository.RedditPostRepository

/**
 * Created by Alexander on 04.01.2018.
 */
class SubredditViewModel(private val repository: RedditPostRepository) : ViewModel() {
    private val subredditName = MutableLiveData<String>()
    private val repoResult = map(subredditName, {
        repository.postsOfSubreddit(it, 30)
    })
    val posts = switchMap(repoResult, { it.pagedList })
    val networkState = switchMap(repoResult, { it.networkState })
    val refreshState = switchMap(repoResult, { it.refreshState })

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun showSubreddit(subreddit: String): Boolean {
        if (subredditName.value == subreddit) {
            return false
        }
        subredditName.value = subreddit
        return true
    }
    fun retry() {
        repoResult.value?.retry?.invoke()
    }

    fun currentSubreddit(): String? = subredditName.value
}