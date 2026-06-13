package com.goinggoing.goinggoing.domain.auth.dto;

public record AuthTokenResponse(
		Long userId,
		String accessToken,
		String refreshToken,
		String tokenType,
		Long accessTokenExpiresInSeconds
) {
}
