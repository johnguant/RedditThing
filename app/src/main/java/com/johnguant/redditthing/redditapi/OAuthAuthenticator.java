package com.johnguant.redditthing.redditapi;

import android.content.Context;

import com.johnguant.redditthing.auth.RedditAuthManager;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class OAuthAuthenticator implements Authenticator{

    private Context mContext;

    public OAuthAuthenticator(Context context){
        mContext = context;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if(responseCount(response) >= 2) {
            return  null;
        }

        Request.Builder builder = response.request().newBuilder();
        RedditAuthManager.getInstance(mContext).invalidateToken();
        builder.header("Authorization", "bearer " + RedditAuthManager.getInstance(mContext).getAccessToken());
        return  builder.build();
    }

    private int responseCount(Response response){
        int result = 1;

        while((response = response.priorResponse()) != null){
            result++;
        }
        return result;
    }
}
