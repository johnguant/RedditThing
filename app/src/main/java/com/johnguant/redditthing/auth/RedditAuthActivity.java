package com.johnguant.redditthing.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.johnguant.redditthing.R;
import com.johnguant.redditthing.redditapi.RedditRequest;
import com.johnguant.redditthing.VolleyQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RedditAuthActivity extends AppCompatActivity {

    public final static String ARG_ACCOUNT_TYPE = "com.johnguant.redditthing";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    private AccountManager mAccountManager;
    private Bundle mResultBundle = null;
    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_auth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAccountAuthenticatorResponse =
                getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }

        mAccountManager = AccountManager.get(getBaseContext());
        WebView loginView = (WebView) findViewById(R.id.login_webview);
        loginView.setWebViewClient(new WebViewClient() {
            boolean authComplete = false;
            Intent result = new Intent();

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                if (url.contains("code=") && !authComplete) {
                    authComplete = true;
                    new AsyncTask<String, Void, Void>() {
                        @Override
                        protected Void doInBackground(String... url) {
                            long time = System.currentTimeMillis();
                            Log.d("redditThing", "response");
                            Uri uri = Uri.parse(url[0]);
                            final String authCode = uri.getQueryParameter("code");
                            OAuthToken oAuthToken = getAccessTokenFromCode(authCode);
                            String username = getUsername(oAuthToken);
                            final Account account = new Account(username, getString(R.string.account_type));
                            mAccountManager.addAccountExplicitly(account, "", null);
                            mAccountManager.setAuthToken(account, "accessToken", oAuthToken.access_token);
                            mAccountManager.setUserData(account, "refreshToken", oAuthToken.refresh_token);
                            mAccountManager.setUserData(account, "expiryTime", String.valueOf(time + (oAuthToken.expiresIn*1000)));
                            final Intent intent = new Intent();
                            intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
                            intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, getString(R.string.account_type));
                            intent.putExtra(AccountManager.KEY_AUTHTOKEN, oAuthToken.access_token);

                            mResultBundle = result.getExtras();
                            finish();
                            return null;
                        }
                    }.execute(url);
                }
            }
        });
        loginView.loadUrl("https://www.reddit.com/api/v1/authorize.compact?client_id=3_XCTkayxEPJuA&response_type=code&state=testing&redirect_uri=com.johnguant.redditthing://oauth2redirect&duration=permanent&scope=read%20privatemessages%20report%20identity%20livemanage%20account%20edit%20history%20flair%20creddits%20subscribe%20vote%20mysubreddits%20submit%20save%20modcontributors%20modmail%20modconfig%20modlog%20modposts%20modflair%20modothers%20modtraffic%20modwiki%20modself");
    }

    public String getUsername(OAuthToken oAuthToken){
        String userUrl = "https://oauth.reddit.com/api/v1/me";
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        RedditRequest redditRequest = new RedditRequest(Request.Method.GET, userUrl, null, future, future);
        redditRequest.addHeader("Authorization", "bearer " + oAuthToken.access_token);
        VolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(redditRequest);
        try {
            JSONObject response = future.get(10, TimeUnit.SECONDS);
            return response.getString("name");
        } catch (InterruptedException | ExecutionException | TimeoutException | JSONException e) {
            return null;
        }
    }

    public OAuthToken getAccessTokenFromCode(String code){
        String codeUrl = "https://www.reddit.com/api/v1/access_token";
        Map<String, String> data = new HashMap<>();
        data.put("grant_type", "authorization_code");
        data.put("code", code);
        data.put("redirect_uri", "com.johnguant.redditthing://oauth2redirect");
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
        VolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(redditRequest);
        JSONObject response;
        try {
            response = future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return null;
        }

        OAuthToken token = null;

        if(response != null) {
            try {
                String accessToken = response.getString("access_token");
                String refreshToken = response.getString("refresh_token");
                int expiresIn = response.getInt("expires_in");
                token = new OAuthToken(accessToken, refreshToken, expiresIn);
            } catch (JSONException e) {
                Log.d("redditThing", "Getting access or refresh token from json failed");
            }
        }
        return token;
    }

    public void finish() {
        if (mAccountAuthenticatorResponse != null) {
            if (mResultBundle != null) {
                mAccountAuthenticatorResponse.onResult(mResultBundle);
            } else {
                mAccountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED,
                        "canceled");
            }
            mAccountAuthenticatorResponse = null;
        }
        super.finish();

    }
}
