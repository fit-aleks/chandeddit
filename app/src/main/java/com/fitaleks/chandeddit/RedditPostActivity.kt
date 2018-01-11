package com.fitaleks.chandeddit

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.View
import com.bumptech.glide.Glide
import com.fitaleks.chandeddit.data.RedditPost
import com.fitaleks.chandeddit.data.Resource
import kotlinx.android.synthetic.main.activity_reddit_post.*

/**
 * Created by alex206512252 on 1/8/18.
 */
class RedditPostActivity : AppCompatActivity() {
    companion object {
        val PARAM_POST_ID = "reddit_post_id"
    }

    private lateinit var model: CertainPostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reddit_post)
        details_text.movementMethod = ScrollingMovementMethod()
        model = getViewModel()
        initData()
    }

    private fun getViewModel(): CertainPostViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CertainPostViewModel(ServiceLocator.getInstance(this@RedditPostActivity)
                        .getRepository()) as T
            }
        })[CertainPostViewModel::class.java]
    }

    private fun initData() {
        model.showPost(intent.getStringExtra(PARAM_POST_ID))
        model.repoResult.observe(this, Observer<Resource<RedditPost>> {
            if (it?.data?.thumbnail?.startsWith("https") == true) {
                Glide.with(this)
                        .load(it.data.thumbnail)
                        .into(details_image)
            } else {
                details_image.visibility = View.GONE
            }

            details_text.text = it?.data?.selftext
        })
    }

}