package com.goinggoing.goinggoing.domain.auth.dto;

public record LoginRequest(
		String email,
		String password
) {
}
