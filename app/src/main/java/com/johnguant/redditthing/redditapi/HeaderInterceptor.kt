package com.johnguant.redditthing.redditapi

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val requestBuilder = original.newBuilder()
                .header("User-Agent", "android:com.johnguant.redditthing:v0.0.1 (by /u/john_guant)")
                .method(original.method(), original.body())
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
