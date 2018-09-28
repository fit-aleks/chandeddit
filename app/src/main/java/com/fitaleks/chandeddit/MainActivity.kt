package com.fitaleks.chandeddit

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.fitaleks.chandeddit.repository.NetworkState
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val DEFAULT_SUBREDDIT = "androiddev"
    private lateinit var model: SubredditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        model = getViewModel()
        initAdapter()
        initSwipeToRefresh()
        model.showSubreddit(DEFAULT_SUBREDDIT)
    }

    private fun getViewModel(): SubredditViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SubredditViewModel(ServiceLocator.getInstance(this@MainActivity)
                        .getRepository()) as T
            }
        })[SubredditViewModel::class.java]
    }

    private fun initAdapter() {
        val glide = Glide.with(this)
        val adapter = PostsAdapter(glide) {
            model.retry()
        }
        list.adapter = adapter
        model.posts.observe(this, Observer { adapter.submitList(it) })
        model.networkState.observe(this, Observer { adapter.setNetworkState(it) })
    }

    private fun initSwipeToRefresh() {
        model.refreshState.observe(this, Observer {
            swipe_refresh_layout.isRefreshing = it == NetworkState.LOADING
        })
        swipe_refresh_layout.setOnRefreshListener {
            model.refresh()
        }
    }

}
