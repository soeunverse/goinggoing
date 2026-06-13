package com.goinggoing.goinggoing.domain.route.dto;

import com.goinggoing.goinggoing.domain.content.entity.ContentType;

import java.math.BigDecimal;

public record RoutePlaceResponse(
		Long routePlaceId,
		Long contentId,
		String name,
		ContentType placeType,
		String address,
		BigDecimal latitude,
		BigDecimal longitude,
		Integer dayNumber,
		Integer visitOrder,
		Integer estimatedStayMinutes,
		Integer moveMinutesFromPrevious,
		String recommendationNote
) {
}
