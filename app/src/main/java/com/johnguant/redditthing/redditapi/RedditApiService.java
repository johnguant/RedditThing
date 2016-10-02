package com.johnguant.redditthing.redditapi;

import com.johnguant.redditthing.redditapi.model.Account;
import com.johnguant.redditthing.redditapi.model.Link;
import com.johnguant.redditthing.redditapi.model.Listing;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RedditApiService {
    @GET(".json?raw_json=1")
    Call<Listing<Link>> loadFrontpage(@Query("after") Link after);

    @GET("/api/v1/me")
    Call<Account> getMe();
}
