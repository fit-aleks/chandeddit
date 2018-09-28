package com.fitaleks.chandeddit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
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
        const val PARAM_POST_ID = "reddit_post_id"
        const val PARAM_POST_ID_WITH_KIND = "reddit_post_id_with_kind"
        val TAG: String = RedditPostActivity::class.java.simpleName
    }

    private lateinit var model: CertainPostViewModel
    private val adapter = PostDetailsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reddit_post)
        model = getViewModel()
        initData()

        details_recycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        details_recycler.adapter = adapter
        details_recycler.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))

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
//            if (it?.data?.thumbnail?.startsWith("https") == true) {
//                Glide.with(this)
//                        .load(it.data.thumbnail)
//                        .into(details_image)
//            } else {
//                details_image.visibility = View.GONE
//            }

            it?.data?.let { post ->
                adapter.setRedditPost(post)
            }

        })
    }

    private fun initComments() {
        RedditCommentsApi.create().getCommentsForPost("androiddev", intent.getStringExtra(PARAM_POST_ID)).enqueue(object : Callback<List<RedditComment>> {
            override fun onFailure(call: Call<List<RedditComment>>?, t: Throwable?) {
                Log.e(TAG, t.toString())
                t?.printStackTrace()

            }

            override fun onResponse(call: Call<List<RedditComment>>?, response: Response<List<RedditComment>>?) {
                response?.body()?.let {
                    adapter.setComments(it)
                    it.forEach {
                        Log.d(TAG, "Success! ${it.author}  ${it.body}")
                    }
                }
            }
        })
    }

}