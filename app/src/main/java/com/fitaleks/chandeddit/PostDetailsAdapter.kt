package com.fitaleks.chandeddit

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by Alexander on 03.04.2018.
 */
class PostDetailsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_IMAGE = 0
    private val TYPE_TEXT = 1
    private val TYPE_COMMENTS = 2
    private var mainText: String? = null
    private var mainImage: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_TEXT) {
            return PostTextViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post_text, parent, false))
        } else {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) TYPE_IMAGE else if (position == 1) TYPE_TEXT
        return TYPE_TEXT
    }

    override fun getItemCount(): Int {
        var numOfItems = if (mainText == null) 0 else 1
        if (mainImage != null && mainImage?.startsWith("https") == true) {
            numOfItems++
        }
        return numOfItems
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (position == 0) {
            (holder as PostTextViewHolder).textView.text = mainText
        }
    }

    fun setMainText(text: String?) {
        mainText = text
        mainText?.let {
            //            notifyItemChanged(0)
            notifyDataSetChanged()
        }
    }
}

class PostTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textView: TextView = itemView.findViewById(R.id.item_post_details_text)
}

class PostImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textView: ImageView = itemView.findViewById(R.id.item_post_details_image)
}