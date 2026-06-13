package com.goinggoing.goinggoing.global.security;

public interface AuthTokenGenerator {

	String generateAccessToken();

	String generateRefreshToken();
}
