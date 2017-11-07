package com.johnguant.redditthing.auth

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.accounts.NetworkErrorException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils

import com.johnguant.redditthing.redditapi.model.OAuthToken

class RedditAuthenticator(private val context: Context) : AbstractAccountAuthenticator(context) {

    override fun editProperties(accountAuthenticatorResponse: AccountAuthenticatorResponse, s: String): Bundle? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun addAccount(response: AccountAuthenticatorResponse, accountType: String, authTokenType: String?, requiredFeatures: Array<String>?, options: Bundle?): Bundle? {
        val intent = Intent(this.context, RedditAuthActivity::class.java)
        intent.putExtra(RedditAuthActivity.ARG_ACCOUNT_TYPE, accountType)
        intent.putExtra(RedditAuthActivity.ARG_AUTH_TYPE, authTokenType)
        intent.putExtra(RedditAuthActivity.ARG_IS_ADDING_NEW_ACCOUNT, true)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(accountAuthenticatorResponse: AccountAuthenticatorResponse, account: Account, bundle: Bundle): Bundle? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun getAuthToken(response: AccountAuthenticatorResponse, account: Account, authTokenType: String, bundle: Bundle): Bundle {
        val am = AccountManager.get(context)

        var authToken = am.peekAuthToken(account, authTokenType)

        if (TextUtils.isEmpty(authToken)) {
            val time = System.currentTimeMillis()
            val token = RedditAuthManager.getInstance(context)!!.getNewAuthToken(account, am)
            am.setAuthToken(account, authTokenType, token!!.accessToken)
            am.setUserData(account, "expiryTime", (time + token.expiresIn * 1000).toString())
            authToken = token.accessToken
        }

        val result = Bundle()
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
        result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
        return result
    }

    override fun getAuthTokenLabel(s: String): String? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun updateCredentials(accountAuthenticatorResponse: AccountAuthenticatorResponse, account: Account, s: String, bundle: Bundle): Bundle? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun hasFeatures(accountAuthenticatorResponse: AccountAuthenticatorResponse, account: Account, strings: Array<String>): Bundle? {
        return null
    }
}
