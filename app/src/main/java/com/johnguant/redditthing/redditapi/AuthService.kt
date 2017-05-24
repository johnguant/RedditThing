package com.johnguant.redditthing.redditapi

import com.johnguant.redditthing.redditapi.model.OAuthToken

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {

    @FormUrlEncoded
    @POST("https://www.reddit.com/api/v1/access_token")
    fun accessToken(@Field("grant_type") grantType: String,
                    @Field("code") code: String,
                    @Field("redirect_uri") redirectUri: String): Call<OAuthToken>

    @FormUrlEncoded
    @POST("https://www.reddit.com/api/v1/access_token")
    fun deviceAccessToken(@Field("grant_type") grantType: String,
                          @Field("device_id") deviceId: String): Call<OAuthToken>

    @FormUrlEncoded
    @POST("https://www.reddit.com/api/v1/access_token")
    fun refreshAccessToken(@Field("grant_type") grantType: String,
                           @Field("refresh_token") refreshToken: String): Call<OAuthToken>
}
