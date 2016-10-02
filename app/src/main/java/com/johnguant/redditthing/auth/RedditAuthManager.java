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

import com.johnguant.redditthing.redditapi.AuthService;
import com.johnguant.redditthing.redditapi.ServiceGenerator;
import com.johnguant.redditthing.redditapi.model.OAuthToken;

import java.io.IOException;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Response;

public class RedditAuthManager {

    static RedditAuthManager mInstance;
    private Context context;

    RedditAuthManager(Context ctx) {
        context = ctx;
    }

    public synchronized String getAccessToken() {
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

    public void invalidateToken() {
        AccountManager.get(context).invalidateAuthToken("com.johnguant.redditthing", getAccessToken());
    }

    public String getAppOAuth(){
        SharedPreferences authPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        String token = authPref.getString("accessToken", null);
        if(!TextUtils.isEmpty(token)){
            Long expiry = authPref.getLong("expiryTime", 0);
            if(System.currentTimeMillis() < expiry){
                return token;
            }
        }
        // If we get to here token is invalid so get new token from reddit
        SharedPreferences.Editor authPrefEdit = authPref.edit();
        Long time = System.currentTimeMillis();
        String deviceId = authPref.getString("deviceId", null);
        if(deviceId == null){
            deviceId = UUID.randomUUID().toString();
            authPrefEdit.putString("deviceId", deviceId);
        }
        AuthService service = ServiceGenerator.createService(AuthService.class, context);
        Call<OAuthToken> call = service.deviceAccessToken("https://oauth.reddit.com/grants/installed_client", deviceId);
        OAuthToken newToken;
        try {
            Response<OAuthToken> response = call.execute();
            newToken = response.body();
        } catch (IOException e) {
            return null;
        }
        token = newToken.getAccessToken();
        authPrefEdit.putString("accessToken", token);
        authPrefEdit.putLong("expiryTime", time + (newToken.getExpiresIn()*1000));
        authPrefEdit.apply();
        return token;
    }

    public OAuthToken getNewAuthToken(Account account, AccountManager am){
        String refreshToken = am.getUserData(account, "refreshToken");
        AuthService service = ServiceGenerator.createService(AuthService.class, context);
        Call<OAuthToken> call = service.refreshAccessToken("refresh_token", refreshToken);
        OAuthToken token;
        try {
            token = call.execute().body();
        } catch (IOException e) {
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
