package com.bnpinnovation.turret.helper;

public class Tokens {
    public Tokens(String refreshToken, String accessToken) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }

    private final String accessToken;
    private final String refreshToken;


    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
