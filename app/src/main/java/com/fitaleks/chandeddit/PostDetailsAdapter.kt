package com.fitaleks.chandeddit

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.fitaleks.chandeddit.data.RedditComment

/**
 * Created by Alexander on 03.04.2018.
 */
class PostDetailsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_IMAGE = 0
    private val TYPE_TEXT = 1
    private val TYPE_COMMENTS = 2
    private var mainText: String? = null
    private var mainImage: String? = null
    private var comments: List<RedditComment>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TEXT -> PostTextViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post_text, parent, false))
            TYPE_COMMENTS -> CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post_comment, parent, false))
            else -> TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return TYPE_TEXT
        else if (comments != null && (position - 1) < (comments?.size ?: 0)) return TYPE_COMMENTS
        return TYPE_TEXT
    }

    override fun getItemCount(): Int {
        var numOfItems = if (mainText == null) 0 else 1
        if (mainImage != null && mainImage?.startsWith("https") == true) {
            numOfItems++
        }
        comments?.let {
            numOfItems += it.size
        }
        return numOfItems
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (position == 0) {
            (holder as PostTextViewHolder).textView.text = mainText
        } else {
            comments?.let {
                val comment = it[position - 1]
                (holder as CommentViewHolder).apply {
                    this.authorTextView.text = comment.author
                    this.commentTextView.text = comment.body
                }
            }
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
    val textView: TextView = itemView.findViewById(R.id.item_post_details_text)
}

class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val authorTextView: TextView = itemView.findViewById(R.id.item_post_comment_author)
    val commentTextView: TextView = itemView.findViewById(R.id.item_post_comment_text)
}

class PostImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textView: ImageView = itemView.findViewById(R.id.item_post_details_image)
}