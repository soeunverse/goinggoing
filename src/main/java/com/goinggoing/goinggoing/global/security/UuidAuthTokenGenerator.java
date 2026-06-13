package com.goinggoing.goinggoing.global.security;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidAuthTokenGenerator implements AuthTokenGenerator {

	@Override
	public String generateAccessToken() {
		return UUID.randomUUID().toString();
	}

	@Override
	public String generateRefreshToken() {
		return UUID.randomUUID().toString();
	}
}
