package com.goinggoing.goinggoing.domain.bookmark.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "찜 추가 요청")
public record BookmarkCreateRequest(
		@Schema(description = "찜할 컨텐츠 ID", example = "1")
		Long contentId
) {
}
