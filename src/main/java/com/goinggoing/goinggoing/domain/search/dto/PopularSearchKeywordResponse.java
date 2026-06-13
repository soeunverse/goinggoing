package com.goinggoing.goinggoing.domain.search.dto;

public record PopularSearchKeywordResponse(
		String keyword,
		Long searchCount
) {
}
