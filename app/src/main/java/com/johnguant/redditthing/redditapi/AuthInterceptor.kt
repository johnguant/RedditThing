package com.johnguant.redditthing.redditapi

import android.content.Context

import com.johnguant.redditthing.auth.RedditAuthManager

import java.io.IOException

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor : Interceptor {

    private var mContext: Context? = null
    private var accessToken: String? = null

    constructor(context: Context) {
        mContext = context
    }

    constructor(accessToken: String) {
        this.accessToken = accessToken
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
        if (original.url().host() == "oauth.reddit.com") {
            if (accessToken != null) {
                requestBuilder.header("Authorization", "bearer " + accessToken)
            } else {
                requestBuilder.header("Authorization", "bearer " + RedditAuthManager.getInstance(mContext!!)!!.accessToken)
            }
        } else if (original.url().host() == "www.reddit.com") {
            requestBuilder.header("Authorization", Credentials.basic("3_XCTkayxEPJuA", ""))
        }
        requestBuilder.method(original.method(), original.body())
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
