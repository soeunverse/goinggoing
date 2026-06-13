package com.goinggoing.goinggoing.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인증 토큰 응답")
public record AuthTokenResponse(
		@Schema(description = "사용자 ID", example = "1")
		Long userId,
		@Schema(description = "API 요청에 사용할 access token", example = "access-token")
		String accessToken,
		@Schema(description = "토큰 갱신 및 로그아웃에 사용할 refresh token", example = "refresh-token")
		String refreshToken,
		@Schema(description = "인증 타입", example = "Bearer")
		String tokenType,
		@Schema(description = "access token 만료 시간(초)", example = "3600")
		Long accessTokenExpiresInSeconds
) {
}
