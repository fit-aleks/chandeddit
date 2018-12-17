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
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.groupiex.plusAssign
import kotlinx.android.synthetic.main.activity_reddit_post.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by alex206512252 on 1/8/18.
 */
class RedditPostActivity : AppCompatActivity() {
    val LOG_TAG = RedditPostActivity::class.java.simpleName

    companion object {
        const val PARAM_POST_ID = "reddit_post_id"
        const val PARAM_POST_ID_WITH_KIND = "reddit_post_id_with_kind"
        val TAG: String = RedditPostActivity::class.java.simpleName
    }

    private lateinit var model: CertainPostViewModel
    //    private val adapter = PostDetailsAdapter()
    private val groupieAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reddit_post)
        model = getViewModel()

        details_recycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        details_recycler.adapter = groupieAdapter
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

            it?.data?.let { post ->
                Log.d(LOG_TAG, "post data")
                groupieAdapter += Section().apply {
                    add(TextPostItem(post))
                }
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
                    val section = Section()
                    if (it.isNotEmpty()) {
                        section += CommentHeaderItem()
                    }
                    it.forEach { comment ->
                        Log.d(TAG, "Success! ${comment.author} numOfReplies = ${comment.replies?.data?.children?.size} ")

                        buildCommentsSection(comment, section)

                    }
                    groupieAdapter += section
                }
            }
        })
    }

    private fun buildCommentsSection(comment: RedditComment, section: Section) {
        if (comment.author == null || comment.bodyHtml == null) {
            return
        }
        comment.replies?.let { replies ->
            if (replies.data.children.isNotEmpty()) {
                val expandableComment = ExpandableComment(comment)

                section += ExpandableGroup(expandableComment, true).apply {
                    replies.data.children.forEach { commentChild ->
                        buildExpandableContent(commentChild.data, this, 0)
                    }
                }
            }
        } ?: section.add(CommentItem(comment))
    }

    private fun buildExpandableContent(comment: RedditComment, expandableGroup: ExpandableGroup, depth: Int) {
        if (comment.author == null || comment.bodyHtml == null) {
            return
        }
        comment.replies?.let { replies ->
            if (replies.data.children.isNotEmpty()) {
                val expandableComment = ExpandableComment(comment, depth = depth + 1)
                expandableGroup.add(ExpandableGroup(expandableComment, true).apply {
                    replies.data.children.forEach { commentChild ->
                        buildExpandableContent(commentChild.data, this, depth + 1)
                    }
                })
            }
        } ?: expandableGroup.add(CommentItem(comment, depth = depth + 1))
    }

}