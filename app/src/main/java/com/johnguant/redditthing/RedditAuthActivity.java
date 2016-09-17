package com.johnguant.redditthing;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RedditAuthActivity extends AppCompatActivity {

    public final static String ARG_ACCOUNT_TYPE = "com.johnguant.redditthing";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    private static String accessToken;
    private static String refreshToken;


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

                if (url.contains("code=") && authComplete != true) {
                    Log.d("redditThing", "response");
                    Uri uri = Uri.parse(url);
                    final String authCode = uri.getQueryParameter("code");
                    authComplete = true;
                    String codeUrl = "https://www.reddit.com/api/v1/access_token";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, codeUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                accessToken = jsonResponse.getString("access_token");
                                refreshToken = jsonResponse.getString("refresh_token");
                                Log.d("redditThing", "hi");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String userUrl = "https://oauth.reddit.com//api/v1/me";
                            JsonObjectRequest userRequest = new JsonObjectRequest(Request.Method.GET, userUrl, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String userName = response.getString("name");
                                        final Account account = new Account(userName, getString(R.string.account_type));
                                        mAccountManager.addAccountExplicitly(account, "", null);
                                        mAccountManager.setAuthToken(account, "access_token", accessToken);
                                        mAccountManager.setUserData(account, "refreshToken", refreshToken);
                                        final Intent intent = new Intent();
                                        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, userName);
                                        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, getString(R.string.account_type));
                                        intent.putExtra(AccountManager.KEY_AUTHTOKEN, accessToken);

                                        mResultBundle = result.getExtras();
                                        finish();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("redditThing", "It borked");
                                    finish();
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() {
                                    Map<String, String> headers = new HashMap<>();
                                    headers.put("Content-Type", "application/x-www-form-urlencoded");
                                    headers.put("User-agent", "android:com.johnguant.redditthing:v0.0.1 (by /u/john_guant)");
                                    headers.put("Authorization", "bearer " + accessToken);
                                    return headers;
                                }
                            };
                            VolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(userRequest);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("redditThing", "It borked");
                            finish();
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<>();
                            try {
                                byte[] loginData = "3_XCTkayxEPJuA:".getBytes("UTF-8");
                                String base64 = Base64.encodeToString(loginData, Base64.NO_WRAP);
                                //headers.put("Content-Type", "application/x-www-form-urlencoded");
                                headers.put("User-agent", "android:com.johnguant.redditthing:v0.0.1 (by /u/john_guant)");
                                headers.put("Authorization", "Basic " + base64);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            return headers;
                        }

                        @Override
                        public Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("grant_type", "authorization_code");
                            params.put("code", authCode);
                            params.put("redirect_uri", "com.johnguant.redditthing://oauth2redirect");
                            return params;
                        }
                    };

                    VolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                }
            }
        });
        loginView.loadUrl("https://www.reddit.com/api/v1/authorize.compact?client_id=3_XCTkayxEPJuA&response_type=code&state=testing&redirect_uri=com.johnguant.redditthing://oauth2redirect&duration=permanent&scope=read%20privatemessages%20report%20identity%20livemanage%20account%20edit%20history%20flair%20creddits%20subscribe%20vote%20mysubreddits%20submit%20save%20modcontributors%20modmail%20modconfig%20modlog%20modposts%20modflair%20modothers%20modtraffic%20modwiki%20modself");
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
