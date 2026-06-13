package com.goinggoing.goinggoing.domain.bookmark.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "찜 일괄 삭제 요청")
public record BookmarkDeleteRequest(
		@Schema(description = "삭제할 찜 ID 목록", example = "[1, 2]")
		List<Long> bookmarkIds
) {
}
