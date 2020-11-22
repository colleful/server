package com.colleful.server.security;

public class JwtProperties {

    public static long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 30L;
    public static String HEADER = "Access-Token";
}
