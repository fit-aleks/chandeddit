package com.fitaleks.chandeddit

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.bumptech.glide.Glide
import com.fitaleks.chandeddit.api.RedditCommentsApi
import com.fitaleks.chandeddit.data.RedditComment
import com.fitaleks.chandeddit.data.RedditPost
import com.fitaleks.chandeddit.data.Resource
import kotlinx.android.synthetic.main.activity_reddit_post.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by alex206512252 on 1/8/18.
 */
class RedditPostActivity : AppCompatActivity() {
    companion object {
        val TAG = RedditPostActivity::class.java.simpleName
        val PARAM_POST_ID_WITH_KIND = "reddit_post_id_with_kind"
        val PARAM_POST_ID = "reddit_post_id"
    }

    private lateinit var model: CertainPostViewModel
    private val adapter = PostDetailsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reddit_post)
        model = getViewModel()

        details_recycler.layoutManager = LinearLayoutManager(this)
        details_recycler.adapter = adapter

        initData()
        initComments()
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
        model.showPost(intent.getStringExtra(PARAM_POST_ID_WITH_KIND))
        model.repoResult.observe(this, Observer<Resource<RedditPost>> {
            if (it?.data?.thumbnail?.startsWith("https") == true) {
                Glide.with(this)
                        .load(it.data.thumbnail)
                        .into(details_image)
            } else {
                details_image.visibility = View.GONE
            }

            adapter.setMainText(it?.data?.selftext)
        })
    }

    private fun initComments() {
        RedditCommentsApi.create().getCommentsForPost("androiddev", intent.getStringExtra(PARAM_POST_ID)).enqueue(object : Callback<List<RedditComment>>{
            override fun onFailure(call: Call<List<RedditComment>>?, t: Throwable?) {
                Log.e(TAG, t.toString())
                t?.printStackTrace()

            }

            override fun onResponse(call: Call<List<RedditComment>>?, response: Response<List<RedditComment>>?) {
                response?.body()?.forEach {
                    Log.d(TAG, "Success! ${it.author}  ${it.body}")
                }
            }
        })
    }

}