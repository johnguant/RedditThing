package com.johnguant.redditthing.redditapi

import android.content.Context

import com.johnguant.redditthing.auth.RedditAuthManager

import java.io.IOException

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class OAuthAuthenticator(private val mContext: Context) : Authenticator {

    @Throws(IOException::class)
    override fun authenticate(route: Route, response: Response): Request? {
        if (responseCount(response) >= 2) {
            return null
        }

        val builder = response.request().newBuilder()
        RedditAuthManager.getInstance(mContext)!!.invalidateToken()
        builder.header("Authorization", "bearer " + RedditAuthManager.getInstance(mContext)!!.accessToken)
        return builder.build()
    }

    private fun responseCount(response: Response): Int {
        var response = response
        var result = 1

        while (response.priorResponse() != null) {
            response = response.priorResponse()
            result++
        }
        return result
    }
}
