package com.goinggoing.goinggoing.domain.content.dto;

import com.goinggoing.goinggoing.domain.content.entity.ContentType;

import java.math.BigDecimal;
import java.util.List;

public record ContentDetailResponse(
		Long id,
		String title,
		String summary,
		String description,
		String address,
		BigDecimal latitude,
		BigDecimal longitude,
		String thumbnailUrl,
		ContentType contentType,
		String regionName,
		String themeName,
		String subThemeName,
		Long viewCount,
		Long bookmarkCount,
		boolean hot,
		List<ContentCardResponse> cards,
		List<ContentTagResponse> tags
) {
}
