package com.goinggoing.goinggoing.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그아웃 요청")
public record LogoutRequest(
		@Schema(description = "폐기할 refresh token", example = "refresh-token")
		String refreshToken
) {
}
