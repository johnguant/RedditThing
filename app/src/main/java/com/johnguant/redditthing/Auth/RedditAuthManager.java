package com.johnguant.redditthing.Auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.johnguant.redditthing.RedditApi.RedditRequest;
import com.johnguant.redditthing.VolleyQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RedditAuthManager {

    static RedditAuthManager mInstance;
    static Context context;

    RedditAuthManager(Context ctx){
        context = ctx;
    }

    public OAuthToken getNewAuthToken(Account account, AccountManager am){
        String refreshToken = am.getUserData(account, "refreshToken");
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        String url = "https://www.reddit.com/api/v1/access_token";
        Map<String, String> data = new HashMap<>();
        data.put("grant_type", "refresh_token");
        data.put("refresh_token", refreshToken);
        RedditRequest redditRequest = new RedditRequest(Request.Method.POST, url, data, future, future);
        try {
            byte[] loginData = "3_XCTkayxEPJuA:".getBytes("UTF-8");
            String base64 = Base64.encodeToString(loginData, Base64.NO_WRAP);
            redditRequest.addHeader("Authorization", "Basic " + base64);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        VolleyQueue.getInstance(context).addToRequestQueue(redditRequest);
        OAuthToken token = null;
        try {
            JSONObject result = future.get(10, TimeUnit.SECONDS);
            String accessToken = result.getString("access_token");
            int expiresIn = result.getInt("expires_in");
            token = new OAuthToken(accessToken, null, expiresIn);
        } catch (InterruptedException | ExecutionException | JSONException | TimeoutException e) {
            return null;
        }
        return token;
    }

    public static synchronized RedditAuthManager getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new RedditAuthManager(context.getApplicationContext());
        }
        return mInstance;
    }
}
