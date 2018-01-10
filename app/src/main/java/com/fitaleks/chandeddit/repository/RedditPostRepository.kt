package com.fitaleks.chandeddit.repository

import android.arch.lifecycle.LiveData
import com.fitaleks.chandeddit.data.RedditPost
import com.fitaleks.chandeddit.data.Resource

/**
 * Created by alex206512252 on 1/2/18.
 */
interface RedditPostRepository {
    fun postsOfSubreddit(subreddit: String, itemsPerPage: Int): Listing<RedditPost>
    fun postById(postId: String): LiveData<Resource<RedditPost>>
}