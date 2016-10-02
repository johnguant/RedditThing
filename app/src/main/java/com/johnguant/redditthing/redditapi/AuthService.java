package com.johnguant.redditthing.redditapi;

import com.johnguant.redditthing.redditapi.model.OAuthToken;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthService {

    @FormUrlEncoded
    @POST("https://www.reddit.com/api/v1/access_token")
    Call<OAuthToken> accessToken(@Field("grant_type") String grantType,
                                 @Field("code") String code,
                                 @Field("redirect_uri") String redirectUri);

    @FormUrlEncoded
    @POST("https://www.reddit.com/api/v1/access_token")
    Call<OAuthToken> deviceAccessToken(@Field("grant_type") String grantType,
                                       @Field("device_id") String deviceId);

    @FormUrlEncoded
    @POST("https://www.reddit.com/api/v1/access_token")
    Call<OAuthToken> refreshAccessToken(@Field("grant_type") String grantType,
                                        @Field("refresh_token") String refreshToken);
}
