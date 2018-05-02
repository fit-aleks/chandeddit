package com.fitaleks.chandeddit

import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import com.fitaleks.chandeddit.data.RedditComment
import com.fitaleks.chandeddit.util.timeDiffToStringShort
import java.util.*

/**
 * Created by Alexander on 03.04.2018.
 */
class PostDetailsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_IMAGE = 0
    private val TYPE_TEXT = 1
    private val TYPE_COMMENTS = 2
    private val TYPE_COMMENTS_TITLE = 3
    private var mainText: String? = null
    private var mainImage: String? = null
    private var comments: List<RedditComment>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TEXT -> PostTextViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post_text, parent, false))
            TYPE_COMMENTS -> CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post_comment, parent, false))
            TYPE_COMMENTS_TITLE -> CommentTitleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post_comments_title, parent, false))
            else -> TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> TYPE_TEXT
            position == 1 -> TYPE_COMMENTS_TITLE
            comments != null && (position - 2) < (comments?.size ?: 0) -> TYPE_COMMENTS
            else -> TYPE_TEXT
        }
    }

    override fun getItemCount(): Int {
        // there is always a comments title
        var numOfItems = if (mainText == null) 1 else 2
        if (mainImage != null && mainImage?.startsWith("https") == true) {
            numOfItems++
        }
        comments?.let {
            numOfItems += it.size
        }
        return numOfItems
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (getItemViewType(position)) {
            TYPE_TEXT -> {
                (holder as PostTextViewHolder).apply {
                    mainText?.let {
                        this.textView.loadData(
//                                it.replace("&amp;", "&").replace("&lt;","<").replace("&gt;", ">"),
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                    Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY).toString()
                                else
                                    Html.fromHtml(it).toString(),
                                "text/html",
                                "utf-8")
                    }
//                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//                        this.textView.text = Html.fromHtml(Html.fromHtml(mainText).toString())
//                    } else {
//                        this.textView.text = Html.fromHtml(Html.fromHtml(mainText, Html.FROM_HTML_MODE_LEGACY).toString(), Html.FROM_HTML_MODE_LEGACY)
//                    }
//                    this.textView.movementMethod = LinkMovementMethod.getInstance()
                }

            }
            TYPE_COMMENTS -> {
                comments?.let {
                    val comment = it[position - 2]
                    (holder as CommentViewHolder).apply {
                        val diff = Date().time - Date(comment.createdUtc.toLong() * 1000).time
                        this.authorTextView.text = this.authorTextView.context.getString(R.string.item_reddit_comment_created, comment.author, timeDiffToStringShort(diff))
                        // one convert from html to text is not enough. format requires it to be done twice
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            this.commentTextView.text = Html.fromHtml(Html.fromHtml(comment.bodyHtml, Html.FROM_HTML_MODE_LEGACY).toString(), Html.FROM_HTML_MODE_LEGACY)
                        } else {
                            this.commentTextView.text = Html.fromHtml(Html.fromHtml(comment.bodyHtml).toString())
                        }
                        this.commentTextView.movementMethod = LinkMovementMethod.getInstance()
                    }
                }
            }
            TYPE_COMMENTS_TITLE -> {}
        }
    }

    fun setMainText(text: String?) {
        mainText = text
        mainText?.let {
            //            notifyItemChanged(0)
            notifyDataSetChanged()
        }
    }

    fun setComments(commentsList: List<RedditComment>) {
        comments = commentsList
        comments?.let {
            notifyDataSetChanged()
        }
    }
}

class PostTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textView: WebView = itemView.findViewById(R.id.item_post_details_text)
}

class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val authorTextView: TextView = itemView.findViewById(R.id.item_post_comment_author)
    //    val timeTextView: TextView = itemView.findViewById(R.id.item_post_comment_time)
    val commentTextView: TextView = itemView.findViewById(R.id.item_post_comment_text)
}

class CommentTitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class PostImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textView: ImageView = itemView.findViewById(R.id.item_post_details_image)
}