package com.goinggoing.goinggoing.domain.search.dto;

import java.time.LocalDateTime;

public record RecentSearchKeywordResponse(
		String keyword,
		LocalDateTime searchedAt
) {
}
