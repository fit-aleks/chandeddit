package com.fitaleks.chandeddit

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations.switchMap
import android.arch.lifecycle.ViewModel
import com.fitaleks.chandeddit.repository.RedditPostRepository

/**
 * Created by alex206512252 on 1/9/18.
 */
class CertainPostViewModel(private val repository: RedditPostRepository) : ViewModel() {
    private val postId = MutableLiveData<String>()
    val repoResult = switchMap(postId, {
        repository.postById(it)
    })

    fun showPost(postName: String) {
        if (postId.value == postName) {
            return
        }
        postId.value = postName
    }
}