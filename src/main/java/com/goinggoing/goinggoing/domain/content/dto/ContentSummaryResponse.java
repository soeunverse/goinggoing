package com.goinggoing.goinggoing.domain.content.dto;

import com.goinggoing.goinggoing.domain.content.entity.ContentType;

public record ContentSummaryResponse(
		Long id,
		String title,
		String summary,
		String regionName,
		String themeName,
		String thumbnailUrl,
		ContentType contentType,
		Long viewCount,
		Long bookmarkCount,
		boolean hot
) {
}
