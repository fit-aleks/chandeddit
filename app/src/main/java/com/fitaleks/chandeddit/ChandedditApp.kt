package com.fitaleks.chandeddit

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.util.*

/**
 * Created by alex206512252 on 12/28/17.
 */
const val KEY_PREFERENCES_UUID = "preferences_uuid"
class ChandedditApp : Application() {
    private val LOG_TAG = ChandedditApp::class.java.simpleName

    override fun onCreate() {
        super.onCreate()

        var uuid = getUUID()
        if (uuid.isEmpty()) {
            uuid = UUID.randomUUID().toString()
        }
        Log.d(LOG_TAG, "uuid = $uuid")
    }

    private fun getUUID(): String {
        val sp:SharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        return sp.getString(KEY_PREFERENCES_UUID, "")
    }
}