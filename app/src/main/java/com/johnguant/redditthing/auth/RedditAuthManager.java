package com.johnguant.redditthing.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.johnguant.redditthing.redditapi.RedditRequest;
import com.johnguant.redditthing.VolleyQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RedditAuthManager {

    static RedditAuthManager mInstance;
    private Context context;

    RedditAuthManager(Context ctx) {
        context = ctx;
    }

    public String getAccessToken() {
        AccountManager am = AccountManager.get(context);
        @SuppressWarnings("MissingPermission")
        Account[] accounts = am.getAccountsByType("com.johnguant.redditthing");
        if(accounts.length > 0){
            AccountManagerFuture<Bundle> accountFuture = am.getAuthToken(accounts[0], "accessToken", null, false, null, null);
            try {
                Bundle authTokenBundle = accountFuture.getResult();
                return authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString();
            } catch (OperationCanceledException | IOException | NullPointerException | AuthenticatorException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return getAppOAuth();
        }
    }

    public String getAppOAuth(){
        SharedPreferences authPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        String token = authPref.getString("accessToken", null);
        if(TextUtils.isEmpty(token)){
            Long expiry = authPref.getLong("expiryTime", 0);
            if(System.currentTimeMillis() < expiry){
                return token;
            }
        }
        // If we get to here token is invalid so get new token from reddit
        SharedPreferences.Editor authPrefEdit = authPref.edit();
        Long time = System.currentTimeMillis();
        String codeUrl = "https://www.reddit.com/api/v1/access_token";
        Map<String, String> data = new HashMap<>();
        data.put("grant_type", "https://oauth.reddit.com/grants/installed_client");
        String deviceId = authPref.getString("deviceId", null);
        if(deviceId == null){
            deviceId = UUID.randomUUID().toString();
            authPrefEdit.putString("deviceId", deviceId);
        }
        data.put("device_id", deviceId);
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        final RedditRequest redditRequest = new RedditRequest(Request.Method.POST, codeUrl,
                data, future, future);
        try {
            byte[] loginData = "3_XCTkayxEPJuA:".getBytes("UTF-8");
            String base64 = Base64.encodeToString(loginData, Base64.NO_WRAP);
            redditRequest.addHeader("Authorization", "Basic " + base64);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        VolleyQueue.getInstance(context).addToRequestQueue(redditRequest);
        JSONObject response;
        try {
            response = future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return null;
        }

        if(response != null) {
            try {
                token = response.getString("access_token");
                int expiresIn = response.getInt("expires_in");
                authPrefEdit.putString("accessToken", token);
                authPrefEdit.putLong("expiryTime", time + (expiresIn*1000));
            } catch (JSONException e) {
            }
        }
        authPrefEdit.apply();
        return token;
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
        OAuthToken token;
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
