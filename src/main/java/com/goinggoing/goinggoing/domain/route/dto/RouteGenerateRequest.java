package com.goinggoing.goinggoing.domain.route.dto;

import com.goinggoing.goinggoing.domain.user.entity.TripDurationType;

public record RouteGenerateRequest(
		Long contentId,
		TripDurationType tripDurationType
) {
}
