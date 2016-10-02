package com.johnguant.redditthing.redditapi.model;

public class OAuthToken {
    private String access_token;
    private String token_type;
    private int expires_in;
    private String scope;
    private String refresh_token;

    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String accessToken) {
        this.access_token = accessToken;
    }

    public String getTokenType() {
        return token_type;
    }

    public void setTokenType(String tokenType) {
        this.token_type = tokenType;
    }

    public int getExpiresIn() {
        return expires_in;
    }

    public void setExpiresIn(int expiresIn) {
        this.expires_in = expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public void setRefreshToken(String refreshToken) {
        this.refresh_token = refreshToken;
    }
}
