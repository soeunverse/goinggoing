package com.goinggoing.goinggoing.domain.bookmark.dto;

import java.util.List;

public record BookmarkDeleteRequest(
		List<Long> bookmarkIds
) {
}
