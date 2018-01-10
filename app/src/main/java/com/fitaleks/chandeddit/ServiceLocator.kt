package com.fitaleks.chandeddit

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.fitaleks.chandeddit.api.RedditApi
import com.fitaleks.chandeddit.db.RedditDb
import com.fitaleks.chandeddit.repository.DbRedditPostRepository
import com.fitaleks.chandeddit.repository.RedditPostRepository
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by alex206512252 on 1/2/18.
 */

interface ServiceLocator {
    companion object {
        private val LOCK = Any()
        private var instance: ServiceLocator? = null

        fun getInstance(context: Context): ServiceLocator {
            synchronized(LOCK) {
                if (instance == null) {
                    instance = DefaultServiceLocator(context.applicationContext as Application)
                }
                return instance!!
            }
        }
    }

    fun getRepository(): RedditPostRepository
    fun getNetworkExecutor(): Executor
    fun getMainThreadExecutor(): Executor
    fun getDiskIOExecutor(): Executor
    fun getRedditApi(): RedditApi
}

class DefaultServiceLocator(val app: Application) : ServiceLocator {
    // thread pool used for disk access
    private val DISK_IO = Executors.newSingleThreadExecutor()

    // thread pool used for network requests
    private val NETWORK_IO = Executors.newFixedThreadPool(5)

    private val MAIN_THREAD = MainThreadExecutor()

    private val db by lazy {
        RedditDb.create(app)
    }

    private val api by lazy {
        RedditApi.create()
    }

    override fun getRepository(): RedditPostRepository {
        return DbRedditPostRepository(db = db,
                redditApi = api,
                mainThreadExecutor = getMainThreadExecutor(),
                ioExecutor = getDiskIOExecutor())
    }

    override fun getNetworkExecutor(): Executor = NETWORK_IO

    override fun getMainThreadExecutor(): Executor = MAIN_THREAD

    override fun getDiskIOExecutor(): Executor = DISK_IO

    override fun getRedditApi(): RedditApi = api

}

class MainThreadExecutor : Executor {
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    override fun execute(command: Runnable?) {
        mainThreadHandler.post(command)
    }
}