package com.goinggoing.goinggoing.global.security;

import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtAuthTokenProviderTest {

	private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");
	private static final String SECRET = "goinggoing-jwt-test-secret-goinggoing-jwt-test-secret";

	private final JwtAuthTokenProvider jwtAuthTokenProvider = new JwtAuthTokenProvider(SECRET, ZONE_ID);

	@Test
	@DisplayName("JWT access token에서 사용자 ID를 추출한다")
	void extractUserIdFromAccessToken() {
		LocalDateTime expiresAt = LocalDateTime.ofInstant(Instant.parse("2026-06-13T01:00:00Z"), ZONE_ID);
		String accessToken = jwtAuthTokenProvider.generateAccessToken(1L, expiresAt);

		Long userId = jwtAuthTokenProvider.extractUserId(accessToken);

		assertThat(userId).isEqualTo(1L);
	}

	@Test
	@DisplayName("형식이 잘못된 access token이면 UNAUTHORIZED 예외가 발생한다")
	void invalidAccessTokenFails() {
		assertThatThrownBy(() -> jwtAuthTokenProvider.extractUserId("invalid-token"))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED));
	}
}
