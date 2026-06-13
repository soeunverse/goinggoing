package com.goinggoing.goinggoing.domain.recommendation.dto;

import com.goinggoing.goinggoing.domain.content.entity.ContentType;

import java.util.List;

public record RouletteRecommendationCondition(
		Long regionId,
		Long themeId,
		Long subThemeId,
		ContentType contentType,
		List<Long> tagIds
) {
}
