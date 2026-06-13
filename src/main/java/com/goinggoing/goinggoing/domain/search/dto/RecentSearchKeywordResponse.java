package com.goinggoing.goinggoing.domain.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "최근 검색어 응답")
public record RecentSearchKeywordResponse(
		@Schema(description = "검색어", example = "성심당")
		String keyword,

		@Schema(description = "최근 검색 시각", example = "2026-06-14T10:00:00")
		LocalDateTime searchedAt
) {
}
