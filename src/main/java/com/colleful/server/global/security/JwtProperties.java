package com.colleful.server.global.security;

public class JwtProperties {

    public static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 30L;
    public static final String HEADER = "Authorization";
    public static final String TYPE = "Bearer";
}
