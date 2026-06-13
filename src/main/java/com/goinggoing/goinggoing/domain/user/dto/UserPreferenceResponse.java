package com.goinggoing.goinggoing.domain.user.dto;

import com.goinggoing.goinggoing.domain.user.entity.TripDurationType;

import java.util.List;

public record UserPreferenceResponse(
		TripDurationType preferredTripDuration,
		List<Long> regionIds,
		List<Long> themeIds,
		List<Long> tagIds
) {

	public static UserPreferenceResponse empty() {
		return new UserPreferenceResponse(null, List.of(), List.of(), List.of());
	}
}
