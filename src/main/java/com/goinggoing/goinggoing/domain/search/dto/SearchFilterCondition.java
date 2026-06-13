package com.goinggoing.goinggoing.domain.search.dto;

import com.goinggoing.goinggoing.domain.content.entity.ContentType;

import java.util.List;

public record SearchFilterCondition(
		Long regionId,
		Long themeId,
		Long subThemeId,
		ContentType contentType,
		List<Long> tagIds
) {
}
