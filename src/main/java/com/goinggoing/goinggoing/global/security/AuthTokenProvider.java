package com.goinggoing.goinggoing.global.security;

import java.time.LocalDateTime;

public interface AuthTokenProvider {

	String generateAccessToken(Long userId, LocalDateTime expiresAt);

	String generateRefreshToken();

	Long extractUserId(String accessToken);
}
