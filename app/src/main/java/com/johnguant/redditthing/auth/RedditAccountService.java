package com.johnguant.redditthing.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class RedditAccountService extends Service{

    @Override
    public void onCreate(){
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        RedditAuthenticator authenticator = new RedditAuthenticator(this);
        return authenticator.getIBinder();
    }
}
