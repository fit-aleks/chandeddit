package com.fitaleks.chandeddit.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Created by alex206512252 on 1/2/18.
 */
@Entity(tableName = "posts")
data class RedditPost(
        @PrimaryKey
        val id: String,
        val name: String, // should be used as post id
        val title: String,
        val selftext: String?,
        @SerializedName("selftext_html")
        val selftextHtml: String?,
//        @SerializedName("subreddit") // this seems mutable but fine for a demo
        @ColumnInfo(collate = ColumnInfo.NOCASE)
        val subreddit: String,
        val thumbnail: String?,
        val url: String?) {
    // to be consistent w/ changing backend order, we need to keep a data like this
    var indexInResponse: Int = -1
}