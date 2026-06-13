package com.goinggoing.goinggoing.domain.route.dto;

import com.goinggoing.goinggoing.domain.user.entity.TripDurationType;

import java.math.BigDecimal;
import java.util.List;

public record RouteResponse(
		Long routeId,
		Long contentId,
		String title,
		String summary,
		TripDurationType tripDurationType,
		Integer totalDistanceMeters,
		Integer totalDurationMinutes,
		BigDecimal mapCenterLatitude,
		BigDecimal mapCenterLongitude,
		List<RoutePlaceResponse> places
) {
}
