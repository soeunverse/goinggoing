package com.goinggoing.goinggoing.domain.user.dto;

import com.goinggoing.goinggoing.domain.user.entity.UserStatus;

public record UserProfileResponse(
		Long userId,
		String email,
		String nickname,
		UserStatus status
) {
}
