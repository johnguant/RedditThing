package com.johnguant.redditthing.auth

import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient

import com.johnguant.redditthing.R
import com.johnguant.redditthing.redditapi.AuthInterceptor
import com.johnguant.redditthing.redditapi.AuthService
import com.johnguant.redditthing.redditapi.HeaderInterceptor
import com.johnguant.redditthing.redditapi.RedditApiService
import com.johnguant.redditthing.redditapi.ServiceGenerator
import com.johnguant.redditthing.redditapi.model.OAuthToken

import java.io.IOException

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RedditAuthActivity : AppCompatActivity() {

    private var mAccountManager: AccountManager? = null
    private var mResultBundle: Bundle? = null
    private var mAccountAuthenticatorResponse: AccountAuthenticatorResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reddit_auth)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        mAccountAuthenticatorResponse = intent.getParcelableExtra<AccountAuthenticatorResponse>(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE)

        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse!!.onRequestContinued()
        }

        mAccountManager = AccountManager.get(baseContext)
        val loginView = findViewById(R.id.login_webview) as WebView
        loginView.setWebViewClient(object : WebViewClient() {
            internal var authComplete = false
            internal var result = Intent()

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
                super.onPageStarted(view, url, favicon)

                if (url.contains("code=") && !authComplete) {
                    authComplete = true
                    object : AsyncTask<String, Void, Void>() {
                        override fun doInBackground(vararg url: String): Void? {
                            val time = System.currentTimeMillis()
                            Log.d("redditThing", "response")
                            val uri = Uri.parse(url[0])
                            val authCode = uri.getQueryParameter("code")
                            val oAuthToken = getAccessTokenFromCode(authCode)
                            val username = getUsername(oAuthToken)
                            val account = Account(username, getString(R.string.account_type))
                            mAccountManager!!.addAccountExplicitly(account, "", null)
                            mAccountManager!!.setAuthToken(account, "accessToken", oAuthToken!!.accessToken)
                            mAccountManager!!.setUserData(account, "refreshToken", oAuthToken.refresh_token)
                            mAccountManager!!.setUserData(account, "expiryTime", (time + oAuthToken.expiresIn * 1000).toString())
                            val intent = Intent()
                            intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username)
                            intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, getString(R.string.account_type))
                            intent.putExtra(AccountManager.KEY_AUTHTOKEN, oAuthToken.accessToken)

                            mResultBundle = result.extras
                            finish()
                            return null
                        }
                    }.execute(url)
                }
            }
        })
        loginView.loadUrl("https://www.reddit.com/api/v1/authorize.compact?client_id=3_XCTkayxEPJuA&response_type=code&state=testing&redirect_uri=com.johnguant.redditthing://oauth2redirect&duration=permanent&scope=read%20privatemessages%20report%20identity%20livemanage%20account%20edit%20history%20flair%20creddits%20subscribe%20vote%20mysubreddits%20submit%20save%20modcontributors%20modmail%20modconfig%20modlog%20modposts%20modflair%20modothers%20modtraffic%20modwiki%20modself")
    }

    fun getUsername(oAuthToken: OAuthToken?): String? {
        val BASE_URL = "https://oauth.reddit.com/"
        val httpClient = OkHttpClient.Builder()
                .addInterceptor(HeaderInterceptor())
                .addInterceptor(AuthInterceptor(oAuthToken!!.accessToken!!))
        val builder = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
        builder.client(httpClient.build())
        val retrofit = builder.build()
        val service = retrofit.create(RedditApiService::class.java)
        val call = service.me
        val account: com.johnguant.redditthing.redditapi.model.Account
        try {
            account = call.execute().body()
        } catch (e: IOException) {
            return null
        }

        return account.name
    }

    fun getAccessTokenFromCode(code: String): OAuthToken? {
        val service = ServiceGenerator.createService(AuthService::class.java, applicationContext)
        val call = service.accessToken("authorization_code", code, "com.johnguant.redditthing://oauth2redirect")
        val token: OAuthToken
        try {
            token = call.execute().body()
        } catch (e: IOException) {
            return null
        }

        return token

    }

    override fun finish() {
        if (mAccountAuthenticatorResponse != null) {
            if (mResultBundle != null) {
                mAccountAuthenticatorResponse!!.onResult(mResultBundle)
            } else {
                mAccountAuthenticatorResponse!!.onError(AccountManager.ERROR_CODE_CANCELED,
                        "canceled")
            }
            mAccountAuthenticatorResponse = null
        }
        super.finish()

    }

    companion object {

        val ARG_ACCOUNT_TYPE = "com.johnguant.redditthing"
        val ARG_AUTH_TYPE = "AUTH_TYPE"
        val ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT"
    }
}
