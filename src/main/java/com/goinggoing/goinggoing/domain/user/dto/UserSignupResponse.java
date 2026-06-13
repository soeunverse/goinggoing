package com.goinggoing.goinggoing.domain.user.dto;

public record UserSignupResponse(
		Long userId,
		String email,
		String nickname
) {
}
