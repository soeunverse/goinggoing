package com.goinggoing.goinggoing.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 요청")
public record UserSignupRequest(
		@Schema(description = "사용자 이메일", example = "user@example.com")
		String email,
		@Schema(description = "비밀번호. 8~100자, 영문과 숫자 포함", example = "password123")
		String password,
		@Schema(description = "사용자 닉네임. 중복 허용", example = "즉흥여행자")
		String nickname
) {
}
