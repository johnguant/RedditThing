package com.johnguant.redditthing.auth

import android.app.Service
import android.content.Intent
import android.os.IBinder

class RedditAccountService : Service() {

    override fun onCreate() {}

    override fun onBind(intent: Intent): IBinder? {
        val authenticator = RedditAuthenticator(this)
        return authenticator.iBinder
    }
}
