package com.example.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecurityConstants {

    private static String SECRET;

    @Value("${SECRET}")
    public void setSecret(String SECRET) {
        SecurityConstants.SECRET = SECRET;
    }

    public static String getSecret() {
        return SECRET;
    }

    private static long EXPIRATION_TIME;

    @Value("${EXPIRATION_TIME}")
    public void setExpirationTime(String EXPIRATION_TIME) {
        SecurityConstants.EXPIRATION_TIME = Long.parseLong(EXPIRATION_TIME.replaceAll("_", ""));
    }

    public static long getExpirationTime() {
        return EXPIRATION_TIME;
    }

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/user/create";
}