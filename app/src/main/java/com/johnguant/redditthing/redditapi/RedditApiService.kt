package com.johnguant.redditthing.redditapi

import com.johnguant.redditthing.redditapi.model.Account
import com.johnguant.redditthing.redditapi.model.Link
import com.johnguant.redditthing.redditapi.model.Listing

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RedditApiService {
    @GET(".json?raw_json=1")
    fun loadFrontpage(@Query("after") after: Link?): Call<Listing<Link>>

    @get:GET("/api/v1/me")
    val me: Call<Account>
}
