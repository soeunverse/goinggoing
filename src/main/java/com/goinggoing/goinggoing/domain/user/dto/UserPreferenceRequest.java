package com.goinggoing.goinggoing.domain.user.dto;

import com.goinggoing.goinggoing.domain.user.entity.TripDurationType;

import java.util.List;

public record UserPreferenceRequest(
		TripDurationType preferredTripDuration,
		List<Long> regionIds,
		List<Long> themeIds,
		List<Long> tagIds
) {
}
