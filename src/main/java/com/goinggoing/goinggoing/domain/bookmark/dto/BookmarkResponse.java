package com.goinggoing.goinggoing.domain.bookmark.dto;

import com.goinggoing.goinggoing.domain.content.entity.ContentType;

import java.time.LocalDateTime;

public record BookmarkResponse(
		Long bookmarkId,
		Long contentId,
		String contentTitle,
		String contentSummary,
		String regionName,
		String themeName,
		String thumbnailUrl,
		ContentType contentType,
		LocalDateTime createdAt
) {
}
