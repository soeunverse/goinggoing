package com.goinggoing.goinggoing.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내 정보 수정 요청")
public record UserProfileUpdateRequest(
		@Schema(description = "수정할 닉네임. 중복 허용", example = "새닉네임")
		String nickname
) {
}
