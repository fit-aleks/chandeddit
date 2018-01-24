package com.fitaleks.chandeddit.api

import android.util.Log
import com.fitaleks.chandeddit.data.RedditComment
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * Created by alex206512252 on 1/22/18.
 */
interface RedditCommentsApi {
    @GET("/r/{subreddit}/comments/{post_id}.json")
    fun getCommentsForPost(@Path("subreddit") subreddit: String, @Path("post_id") postId: String): Call<List<RedditComment>>


    companion object {
        fun create(): RedditCommentsApi {
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { Log.d("API", it) })
            logger.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .build()

            val gson = GsonBuilder()
                    .registerTypeAdapterFactory(CommentTypeAdapterFactory())
                    .create()

            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
//                    .addConverterFactory(EmptyToNullConverter())
//                    .addConverterFactory(CommentsConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(RedditCommentsApi::class.java)
        }
    }

    /*class ListingResponse(val data: CommentsListingData)
    class CommentsListingData(
            val children: List<CommentsData>,
            val after: String?,
            val before: String?
    )

    data class CommentsData(val data: RedditComment)*/
}

class CommentTypeAdapterFactory : TypeAdapterFactory {
    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
        val elemAdapter = gson.getAdapter(JsonElement::class.java)

        val commentsAdapter = CommentsListTypeAdapter(elemAdapter)

        @Suppress("UNCHECKED_CAST")
        return commentsAdapter as TypeAdapter<T>
    }
}

class CommentsListTypeAdapter(val elemAdapter: TypeAdapter<JsonElement>): TypeAdapter<List<RedditComment>>() {
    private val gson = Gson()
    override fun write(out: JsonWriter?, value: List<RedditComment>?) {
    }

    override fun read(jsonReader: JsonReader?): List<RedditComment> {
        var jsonElement = elemAdapter.read(jsonReader)
        if (jsonElement.isJsonArray) {
            val commensData = jsonElement.asJsonArray[1].asJsonObject
            if (commensData.has("data")) {
                jsonElement = commensData["data"]
                if (jsonElement.asJsonObject.has("children")) {
                    jsonElement = jsonElement.asJsonObject["children"]
                }
            }
        }
        val listOfComments = ArrayList<RedditComment>()
        if (jsonElement != null && jsonElement.isJsonArray) {
            val commentsWithKinds = jsonElement.asJsonArray
            (0 until commentsWithKinds.size()).forEach { index ->
                val comment = commentsWithKinds[index].asJsonObject["data"]
                Log.d("PARSER", " ${comment.asJsonObject["author"]} + ${comment.asJsonObject["body"]}")
                listOfComments.add(gson.fromJson(comment, RedditComment::class.java))
            }

        }
        return listOfComments
    }
}