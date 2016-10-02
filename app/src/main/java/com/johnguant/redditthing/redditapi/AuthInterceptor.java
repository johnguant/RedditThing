package com.johnguant.redditthing.redditapi;

import android.content.Context;

import com.johnguant.redditthing.auth.RedditAuthManager;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private Context mContext;
    private String accessToken;

    public AuthInterceptor(Context context) {
        mContext = context;
    }
    public AuthInterceptor(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder();
        if (original.url().host().equals("oauth.reddit.com")) {
            if(accessToken != null){
                requestBuilder.header("Authorization", "bearer " + accessToken);
            } else {
                requestBuilder.header("Authorization", "bearer " + RedditAuthManager.getInstance(mContext).getAccessToken());
            }
        } else if (original.url().host().equals("www.reddit.com")) {
            requestBuilder.header("Authorization", Credentials.basic("3_XCTkayxEPJuA", ""));
        }
        requestBuilder.method(original.method(), original.body());
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
