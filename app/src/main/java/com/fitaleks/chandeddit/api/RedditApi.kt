package com.fitaleks.chandeddit.api

import android.arch.lifecycle.LiveData
import android.util.Log
import com.fitaleks.chandeddit.data.RedditPost
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by alex206512252 on 1/2/18.
 */
interface RedditApi {

    @GET("/r/androiddev.json")
    fun getTop(@Query("limit") limit: Int): Call<ListingResponse>

    @GET("/r/androiddev.json")
    fun getTopAfter(@Query("after") after: String,
                    @Query("limit") limit: Int): Call<ListingResponse>

    @GET("/r/androiddev.json")
    fun getTopBefore(@Query("before") before: String,
                     @Query("limit") limit: Int): Call<ListingResponse>

    @GET("/by_id/{post_id}.json")
    fun getPostsById(@Path("post_id") postId: String): LiveData<Response<ListingResponse>>

    class ListingResponse(val data: ListingData)

    class ListingData(
            val children: List<RedditChildrenResponse>,
            val after: String?,
            val before: String?
    )

    data class RedditChildrenResponse(val data: RedditPost)

    companion object {
        private const val BASE_URL = "https:/reddit.com"
        fun create(): RedditApi {
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { Log.d("API", it) })
            logger.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .build()
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(RedditApi::class.java)
        }
    }
}