package com.fitaleks.chandeddit

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.RequestManager
import com.fitaleks.chandeddit.data.RedditPost
import com.fitaleks.chandeddit.util.timeDiffToString
import java.util.*

/**
 * Created by Alexander on 04.01.2018.
 */
class ItemRedditPostViewHolder(view: View, var glide: RequestManager) : RecyclerView.ViewHolder(view) {
    private val title = view.findViewById<TextView>(R.id.item_title)
    private val image = view.findViewById<ImageView>(R.id.item_image)
    private val createdInfo = view.findViewById<TextView>(R.id.created)
    private lateinit var post: RedditPost

    init {
        view.setOnClickListener { clickedView ->
            val intent = Intent(clickedView.context, RedditPostActivity::class.java)
            intent.putExtra(RedditPostActivity.PARAM_POST_ID, post.name)
            clickedView.context.startActivity(intent)
        }
    }

    companion object {
        fun create(parent: ViewGroup, requestManager: RequestManager): ItemRedditPostViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reddit_post, parent, false)
            return ItemRedditPostViewHolder(view, requestManager)
        }
    }

    fun bind(redditPost: RedditPost?) {
        if (redditPost == null) {
            return
        }
        title.text = redditPost.title
        val creationTime = Date(redditPost.createdUtc.toLong() * 1000)
        val currentTime = Date()
        val diff = currentTime.time - creationTime.time

        createdInfo.text = createdInfo.context.getString(R.string.item_reddit_post_created, timeDiffToString(diff), redditPost.author)
        if (redditPost.thumbnail?.startsWith("http") == true) {
            image.visibility = View.VISIBLE
            glide.load(redditPost.thumbnail)
                    .into(image)
        } else {
            glide.clear(image)
            image.visibility = View.GONE
        }
        this.post = redditPost
    }

}