package com.fitaleks.chandeddit.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Created by alex206512252 on 1/18/18.
 */
@Entity(tableName = "comments")
data class RedditComment(
        @PrimaryKey
        val id: String,
        @SerializedName("parent_id")
        val parentId: String,
        @SerializedName("link_id")
        val linkId: String,
        val body: String,
        val author: String,
        @SerializedName("created_utc")
        val createdAt: Double
)