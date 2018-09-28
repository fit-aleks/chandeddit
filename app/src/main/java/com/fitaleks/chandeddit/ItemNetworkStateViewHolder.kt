package com.fitaleks.chandeddit

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.fitaleks.chandeddit.repository.NetworkState
import com.fitaleks.chandeddit.repository.Status

/**
 * Created by Alexander on 04.01.2018.
 */
class ItemNetworkStateViewHolder(view: View, retryCallback: () -> Unit) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    private val progressBar = view.findViewById<ProgressBar>(R.id.item_progressbar)
    private val errorMsg = view.findViewById<TextView>(R.id.item_error_msg)
    private val btnRetry = view.findViewById<Button>(R.id.item_btn_retry)

    companion object {
        fun create(parent: ViewGroup, retryCallback: () -> Unit): ItemNetworkStateViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_network_state, parent, false)
            return ItemNetworkStateViewHolder(view, retryCallback)
        }
    }

    fun bindTo(networkState: NetworkState?) {
        progressBar.visibility = if (networkState?.status == Status.RUNNING) View.VISIBLE else View.GONE
        errorMsg.visibility = if (networkState?.status == Status.FAILED) View.VISIBLE else View.GONE
        btnRetry.visibility = if (networkState?.status == Status.FAILED) View.VISIBLE else View.GONE
        errorMsg.text = networkState?.errorMsg
    }
}