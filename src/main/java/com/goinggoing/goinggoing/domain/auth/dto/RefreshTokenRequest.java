package com.goinggoing.goinggoing.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 갱신 요청")
public record RefreshTokenRequest(
		@Schema(description = "DB에 저장된 refresh token", example = "refresh-token")
		String refreshToken
) {
}
