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
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.johnguant.redditthing.R;
import com.johnguant.redditthing.redditapi.AuthInterceptor;
import com.johnguant.redditthing.redditapi.AuthService;
import com.johnguant.redditthing.redditapi.HeaderInterceptor;
import com.johnguant.redditthing.redditapi.RedditApiService;
import com.johnguant.redditthing.redditapi.ServiceGenerator;
import com.johnguant.redditthing.redditapi.model.OAuthToken;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
                            mAccountManager.setAuthToken(account, "accessToken", oAuthToken.getAccessToken());
                            mAccountManager.setUserData(account, "refreshToken", oAuthToken.getRefreshToken());
                            mAccountManager.setUserData(account, "expiryTime", String.valueOf(time + (oAuthToken.getExpiresIn()*1000)));
                            final Intent intent = new Intent();
                            intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
                            intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, getString(R.string.account_type));
                            intent.putExtra(AccountManager.KEY_AUTHTOKEN, oAuthToken.getAccessToken());

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
        final String BASE_URL = "https://oauth.reddit.com/";
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(new AuthInterceptor(oAuthToken.getAccessToken()));
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        builder.client(httpClient.build());
        Retrofit retrofit = builder.build();
        RedditApiService service = retrofit.create(RedditApiService.class);
        Call<com.johnguant.redditthing.redditapi.model.Account> call = service.getMe();
        com.johnguant.redditthing.redditapi.model.Account account;
        try {
            account = call.execute().body();
        } catch (IOException e) {
            return null;
        }
        return account.getName();
    }

    public OAuthToken getAccessTokenFromCode(String code){
        AuthService service = ServiceGenerator.createService(AuthService.class, getApplicationContext());
        Call<OAuthToken> call = service.accessToken("authorization_code", code, "com.johnguant.redditthing://oauth2redirect");
        OAuthToken token;
        try {
            token = call.execute().body();
        } catch (IOException e) {
            return null;
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
