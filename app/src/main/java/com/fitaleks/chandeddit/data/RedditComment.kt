package com.fitaleks.chandeddit.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

/**
 * Created by alex206512252 on 1/18/18.
 */
@Entity(tableName = "comments")
data class RedditComment(
        @PrimaryKey
        val id: String,
        @SerializedName("parent_id")
        val parentId: String,
        val score: Int,
        @SerializedName("link_id")
        val linkId: String,
        val body: String,
        @SerializedName("body_html")
        val bodyHtml: String?,
        val author: String?,
        @SerializedName("created_utc")
        val createdUtc: Double) {

    @SerializedName("replies")
    @JsonAdapter(EmptyStringAsNullTypeAdapter::class)
    @Ignore
    var replies: CommentReply? = null
}

data class CommentReply(
        val kind: String,
        val data: CommentData
)

data class CommentData(
        val children: List<CommentChildren>
)

data class CommentChildren(
        val kind: String,
        val data: RedditComment
)

class EmptyStringAsNullTypeAdapter<T> : JsonDeserializer<T> {

    override fun deserialize(jsonElement: JsonElement, typeOfT: Type, context: JsonDeserializationContext): T? {
        if ( jsonElement.isJsonPrimitive) {
            val jsonPrimitive = jsonElement.asJsonPrimitive
            if ( jsonPrimitive.isString && jsonPrimitive.asString.isEmpty() ) {
                return null
            }
        }
        return context.deserialize(jsonElement, typeOfT)
    }
}