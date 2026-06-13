package com.goinggoing.goinggoing.domain.user.dto;

public record UserSignupRequest(
		String email,
		String password,
		String nickname
) {
}
