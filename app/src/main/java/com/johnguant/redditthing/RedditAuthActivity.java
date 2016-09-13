package com.johnguant.redditthing;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RedditAuthActivity extends AppCompatActivity {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_auth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        WebView loginView = (WebView) findViewById(R.id.login_webview);
        loginView.setWebViewClient(new WebViewClient() {
            boolean authComplete = false;
            Intent result = new Intent();

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if(url.contains("com.johnguant.redditthing://oauth2redirect") && authComplete != true){
                    Log.d("redditThing", "response");
                    Uri uri = Uri.parse(url);
                    String authCode = uri.getQueryParameter("code");
                    authComplete = true;
                    String codeUrl = "https://www.reddit.com/api/v1/access_token";
                    final JSONObject data = new JSONObject();
                    try {
                        data.put("grant_type", "authorization_code");
                        data.put("code", authCode);
                        data.put("redirect_uri", "com.johnguant.redditthing://oauth2redirect");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, codeUrl, data, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String accessToken = response.getString("access_token");
                                String refreshToken = response.getString("refresh_token");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = super.getHeaders();
                            try {
                                byte[] loginData = "3_XCTkayxEPJuA:".getBytes("UTF-8");
                                String base64 = Base64.encodeToString(loginData, Base64.NO_WRAP);
                                headers.put("Authorization", "Basic " + base64);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            return headers;
                        }
                    };


                }
            }
        });
        loginView.loadUrl("https://www.reddit.com/api/v1/authorize.compact?client_id=3_XCTkayxEPJuA&response_type=code&state=testing&redirect_uri=com.johnguant.redditthing://oauth2redirect&duration=permanent&scope=read%20privatemessages%20report%20identity%20livemanage%20account%20edit%20history%20flair%20creddits%20subscribe%20vote%20mysubreddits%20submit%20save%20modcontributors%20modmail%20modconfig%20modlog%20modposts%20modflair%20modothers%20modtraffic%20modwiki%20modself");
    }
}
