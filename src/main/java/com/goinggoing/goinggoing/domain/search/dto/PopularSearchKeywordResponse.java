package com.goinggoing.goinggoing.domain.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인기 검색어 응답")
public record PopularSearchKeywordResponse(
		@Schema(description = "검색어", example = "성심당")
		String keyword,

		@Schema(description = "검색 횟수", example = "3")
		Long searchCount
) {
}
