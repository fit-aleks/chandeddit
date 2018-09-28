package com.fitaleks.chandeddit.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.fitaleks.chandeddit.data.RedditComment
import com.fitaleks.chandeddit.data.RedditPost

/**
 * Created by alex206512252 on 1/2/18.
 */
@Database(
        entities = [(RedditPost::class), (RedditComment::class)],
        version = 4,
        exportSchema = false
)
abstract class RedditDb : RoomDatabase() {
    companion object {
        fun create(context: Context) : RedditDb {
            val databaseBuilder = Room.databaseBuilder(context, RedditDb::class.java, "reddit.db")
            return databaseBuilder
                    .fallbackToDestructiveMigration()
                    .build()
        }

    }

    abstract fun posts(): RedditPostDao
}