package com.johnguant.redditthing.auth

import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import com.johnguant.redditthing.R
import com.johnguant.redditthing.redditapi.*
import com.johnguant.redditthing.redditapi.model.OAuthToken
import kotlinx.android.synthetic.main.activity_reddit_auth.*
import okhttp3.OkHttpClient
import org.jetbrains.anko.coroutines.experimental.bg
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RedditAuthActivity : AppCompatActivity() {

    private var mAccountManager: AccountManager? = null
    private var mResultBundle: Bundle? = null
    private var mAccountAuthenticatorResponse: AccountAuthenticatorResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reddit_auth)
        setSupportActionBar(toolbar)

        mAccountAuthenticatorResponse = intent.getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE)

        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse!!.onRequestContinued()
        }

        mAccountManager = AccountManager.get(baseContext)
        login_webview.webViewClient = object : WebViewClient() {
            internal var authComplete = false
            internal var result = Intent()

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                if (url.contains("code=") && !authComplete) {
                    authComplete = true
                    bg {
                        val time = System.currentTimeMillis()
                        Log.d("redditThing", "response")
                        val uri = Uri.parse(url)
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
                    }
                }
            }
        }
        login_webview.loadUrl("https://www.reddit.com/api/v1/authorize.compact?client_id=3_XCTkayxEPJuA&response_type=code&state=testing&redirect_uri=com.johnguant.redditthing://oauth2redirect&duration=permanent&scope=read%20privatemessages%20report%20identity%20livemanage%20account%20edit%20history%20flair%20creddits%20subscribe%20vote%20mysubreddits%20submit%20save%20modcontributors%20modmail%20modconfig%20modlog%20modposts%20modflair%20modothers%20modtraffic%20modwiki%20modself")
    }

    fun getUsername(oAuthToken: OAuthToken?): String? {
        val baseUrl = "https://oauth.reddit.com/"
        val httpClient = OkHttpClient.Builder()
                .addInterceptor(HeaderInterceptor())
                .addInterceptor(AuthInterceptor(oAuthToken!!.accessToken!!))
        val builder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
        builder.client(httpClient.build())
        val retrofit = builder.build()
        val service = retrofit.create(RedditApiService::class.java)
        val call = service.me
        val account: com.johnguant.redditthing.redditapi.model.Account
        try {
            account = call.execute().body()!!
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
            token = call.execute().body()!!
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
