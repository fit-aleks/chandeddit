package com.fitaleks.chandeddit

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.fitaleks.chandeddit.data.RedditPost
import com.fitaleks.chandeddit.repository.NetworkState

/**
 * Created by alex206512252 on 1/3/18.
 */
class PostsAdapter(
        private var glide: RequestManager,
        private var retryCallback: () -> Unit) : PagedListAdapter<RedditPost, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    private var networkState: NetworkState? = null

    private fun hasExtraRow(): Boolean = networkState != null && networkState != NetworkState.LOADED

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder,
                                  position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_network_state -> (holder as ItemNetworkStateViewHolder).bindTo(networkState)
            R.layout.item_reddit_post -> (holder as ItemRedditPostViewHolder).bind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            R.layout.item_reddit_post -> ItemRedditPostViewHolder.create(parent, glide)
            R.layout.item_network_state -> ItemNetworkStateViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun getItemCount(): Int = super.getItemCount() + if (hasExtraRow()) 1 else 0

    override fun getItemViewType(position: Int): Int = if (hasExtraRow() && position == itemCount -1) {
            R.layout.item_network_state
        } else {
            R.layout.item_reddit_post
        }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && newNetworkState != previousState){
            notifyItemChanged(itemCount - 1)
        }
    }

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<RedditPost>() {
            override fun areItemsTheSame(oldItem: RedditPost, newItem: RedditPost): Boolean = oldItem == newItem

            override fun areContentsTheSame(oldItem: RedditPost, newItem: RedditPost): Boolean = oldItem.name == newItem.name
        }
    }

}