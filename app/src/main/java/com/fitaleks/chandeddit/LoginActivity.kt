package com.fitaleks.chandeddit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

/**
 * Created by alex206512252 on 1/2/18.
 */
class LoginActivity: AppCompatActivity() {

    private val TAG: String = MainActivity::class.java.simpleName
    private val AUTH_URL = "https://www.reddit.com/api/v1/authorize.compact?client_id=%s" +
            "&response_type=code&state=%s&redirect_uri=%s&" +
            "duration=permanent&scope=identity"

    private val CLIENT_ID = "9fFV4fnLklRzyA"
    private val REDIRECT_URI = "https://chandeddit.fitaleks.com"
    private val STATE = UUID.randomUUID().toString()
    private val ACCESS_TOKEN_URL = "https://www.reddit.com/api/v1/access_token"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        findViewById<Button>(R.id.login).setOnClickListener {
//            startSignIn()
//        }
    }

    fun startSignIn() {
        val url = String.format(AUTH_URL, CLIENT_ID, STATE, REDIRECT_URI)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (intent != null && intent.action == Intent.ACTION_VIEW) {
            val uri = intent.data
            if (uri!!.getQueryParameter("error") != null) {
                val error = uri.getQueryParameter("error")
                Log.e(TAG, "An error has occurred : " + error)
            } else {
                val state = uri.getQueryParameter("state")
                if (state == STATE) {
                    val code = uri.getQueryParameter("code")
                    getAccessToken(code)
                }
            }
        }
    }

    private fun getAccessToken(code: String?) {
        val client = OkHttpClient()
        val authString = CLIENT_ID + ":"
        val encodedAuthString = Base64.encodeToString(authString.toByteArray(), Base64.NO_WRAP)
        val request = Request.Builder()
                .addHeader("User-Agent", "Sample App")
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url(ACCESS_TOKEN_URL)
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                        "grant_type=authorization_code&code=" + code +
                                "&redirect_uri=" + REDIRECT_URI))
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "ERROR: " + e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val json = response.body()?.string() ?: ""

                try {
                    val data = JSONObject(json)
                    val accessToken = data.optString("access_token")
                    val refreshToken = data.optString("refresh_token")

                    Log.d(TAG, "Access Token = $accessToken")
                    Log.d(TAG, "Refresh Token = $refreshToken")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}