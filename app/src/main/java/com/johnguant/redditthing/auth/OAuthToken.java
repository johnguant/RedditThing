package com.johnguant.redditthing.auth;

public class OAuthToken {
    String access_token;
    String refresh_token;
    int expiresIn;

    public OAuthToken(String access_token, String refresh_token, int expiresIn){
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.expiresIn = expiresIn;
    }
}
