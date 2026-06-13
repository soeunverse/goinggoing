package com.goinggoing.goinggoing.domain.content.dto;

import com.goinggoing.goinggoing.domain.content.entity.ContentType;

import java.math.BigDecimal;
import java.util.List;

public record ContentManagementRequest(
		Long regionId,
		Long themeId,
		Long subThemeId,
		String title,
		ContentType contentType,
		String summary,
		String description,
		String address,
		BigDecimal latitude,
		BigDecimal longitude,
		String thumbnailUrl,
		boolean published,
		List<Long> tagIds,
		List<ContentManagementCardRequest> cards
) {
}
