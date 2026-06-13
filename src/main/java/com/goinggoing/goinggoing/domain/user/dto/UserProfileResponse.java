package com.goinggoing.goinggoing.domain.user.dto;

import com.goinggoing.goinggoing.domain.user.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내 정보 조회 응답")
public record UserProfileResponse(
		@Schema(description = "사용자 ID", example = "1")
		Long userId,
		@Schema(description = "사용자 이메일", example = "user@example.com")
		String email,
		@Schema(description = "사용자 닉네임", example = "즉흥여행자")
		String nickname,
		@Schema(description = "사용자 상태", example = "ACTIVE")
		UserStatus status
) {
}
