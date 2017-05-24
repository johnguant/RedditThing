package com.johnguant.redditthing.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerFuture
import android.accounts.AuthenticatorException
import android.accounts.OperationCanceledException
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils

import com.johnguant.redditthing.redditapi.AuthService
import com.johnguant.redditthing.redditapi.ServiceGenerator
import com.johnguant.redditthing.redditapi.model.OAuthToken

import java.io.IOException
import java.util.UUID

import retrofit2.Call
import retrofit2.Response

class RedditAuthManager internal constructor(private val context: Context) {

    val accessToken: String?
        @Synchronized get() {
            val am = AccountManager.get(context)
            val accounts = am.getAccountsByType("com.johnguant.redditthing")
            if (accounts.size > 0) {
                val accountFuture = am.getAuthToken(accounts[0], "accessToken", null, false, null, null)
                try {
                    val authTokenBundle = accountFuture.result
                    return authTokenBundle.get(AccountManager.KEY_AUTHTOKEN)!!.toString()
                } catch (e: OperationCanceledException) {
                    e.printStackTrace()
                    return null
                } catch (e: IOException) {
                    e.printStackTrace()
                    return null
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                    return null
                } catch (e: AuthenticatorException) {
                    e.printStackTrace()
                    return null
                }

            } else {
                return appOAuth
            }
        }

    fun invalidateToken() {
        AccountManager.get(context).invalidateAuthToken("com.johnguant.redditthing", accessToken)
    }

    // If we get to here token is invalid so get new token from reddit
    val appOAuth: String?
        get() {
            val authPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            var token: String = authPref.getString("accessToken", null)
            if (!TextUtils.isEmpty(token)) {
                val expiry = authPref.getLong("expiryTime", 0)
                if (System.currentTimeMillis() < expiry) {
                    return token
                }
            }
            val authPrefEdit = authPref.edit()
            val time = System.currentTimeMillis()
            var deviceId = authPref.getString("deviceId", null)
            if (deviceId == null) {
                deviceId = UUID.randomUUID().toString()
                authPrefEdit.putString("deviceId", deviceId)
            }
            val service = ServiceGenerator.createService(AuthService::class.java, context)
            val call = service.deviceAccessToken("https://oauth.reddit.com/grants/installed_client", deviceId)
            val newToken: OAuthToken
            try {
                val response = call.execute()
                newToken = response.body()
            } catch (e: IOException) {
                return null
            }

            token = newToken.accessToken!!
            authPrefEdit.putString("accessToken", token)
            authPrefEdit.putLong("expiryTime", time + newToken.expiresIn * 1000)
            authPrefEdit.apply()
            return token
        }

    fun getNewAuthToken(account: Account, am: AccountManager): OAuthToken? {
        val refreshToken = am.getUserData(account, "refreshToken")
        val service = ServiceGenerator.createService(AuthService::class.java, context)
        val call = service.refreshAccessToken("refresh_token", refreshToken)
        val token: OAuthToken
        try {
            token = call.execute().body()
        } catch (e: IOException) {
            return null
        }

        return token
    }

    companion object {

        internal var mInstance: RedditAuthManager? = null

        @Synchronized fun getInstance(context: Context): RedditAuthManager? {
            if (mInstance == null) {
                mInstance = RedditAuthManager(context.applicationContext)
            }
            return mInstance
        }
    }
}
