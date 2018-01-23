package com.fitaleks.chandeddit

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.customtabs.CustomTabsCallback
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsIntent
import android.support.customtabs.CustomTabsServiceConnection
import android.support.v4.content.ContextCompat
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
    private var customTabsClient: CustomTabsClient? = null
    private val packageName = "com.android.chrome"

    private val connection = object: CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(name: ComponentName?, client: CustomTabsClient) {
            customTabsClient = client
            client.warmup(0)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            customTabsClient = null
        }
    }

    init {
        CustomTabsClient.bindCustomTabsService(view.context, packageName, connection)

        view.setOnClickListener { clickedView ->
            if (post.selftext?.isNotEmpty() == true) {
                val intent = Intent(clickedView.context, RedditPostActivity::class.java)
                intent.putExtra(RedditPostActivity.PARAM_POST_ID_WITH_KIND, post.name)
                intent.putExtra(RedditPostActivity.PARAM_POST_ID, post.id)
                clickedView.context.startActivity(intent)
            } else {
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(ContextCompat.getColor(clickedView.context, R.color.colorPrimary))
                        .setCloseButtonIcon(BitmapFactory.decodeResource(clickedView.context.resources, R.drawable.ic_arrow_back_white_24dp))
                        .setShowTitle(true)

                val shareLabel = "share"
                val shareIcon = BitmapFactory.decodeResource(clickedView.context.resources, R.drawable.ic_share_white_24dp)
                val actionIntent = Intent(Intent.ACTION_SEND)
                actionIntent.putExtra(Intent.EXTRA_TEXT, post.url)
                actionIntent.type = "text/plain"
                val pendingIntent = PendingIntent.getActivity(clickedView.context, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                builder.setActionButton(shareIcon, shareLabel, pendingIntent, false)

                builder.build().launchUrl(clickedView.context, Uri.parse(post.url))
            }
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
        customTabsClient?.let {
            val session = it.newSession(CustomTabsCallback())
            session.mayLaunchUrl(Uri.parse(post.url), null, null)
        }
    }

}