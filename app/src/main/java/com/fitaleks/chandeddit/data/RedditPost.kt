package com.fitaleks.chandeddit.data

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

/**
 * Created by alex206512252 on 1/2/18.
 */
@Entity(tableName = "posts")
@TypeConverters(ImageTypeConverter::class)
data class RedditPost(
        @PrimaryKey
        val id: String,
        val name: String, // should be used as post id
        val title: String,
        val selftext: String?,
        @SerializedName("selftext_html")
        val selftextHtml: String?,
        @ColumnInfo(collate = ColumnInfo.NOCASE)
        val subreddit: String,
        val thumbnail: String?,
        val preview: PostPreview?,
        val author: String,
        @SerializedName("created_utc")
        val createdUtc: Double,
        @SerializedName("num_comments")
        val numComments: Int,
        val url: String?) {
    // to be consistent w/ changing backend order, we need to keep a data like this
    var indexInResponse: Int = -1
}

data class PostPreview(val images: List<PostPreviewImage>)

data class PostPreviewImage(val source: ImageSource, val id: String)

data class ImageSource(val url: String)

class ImageTypeConverter {
    @TypeConverter
    fun imagesToString(images: PostPreview?) : String? {
        if (images == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<PostPreview>() {}.type
        return gson.toJson(images, type)
    }

    @TypeConverter
    fun stringToImages(json: String?): PostPreview? {
        if (json == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<PostPreview>() {}.type
        return gson.fromJson(json, type)
    }
}