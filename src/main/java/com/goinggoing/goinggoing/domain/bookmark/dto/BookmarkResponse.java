package com.goinggoing.goinggoing.domain.bookmark.dto;

import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "찜 응답")
public record BookmarkResponse(
		@Schema(description = "찜 ID", example = "1")
		Long bookmarkId,

		@Schema(description = "컨텐츠 ID", example = "1")
		Long contentId,

		@Schema(description = "컨텐츠 제목", example = "성심당")
		String contentTitle,

		@Schema(description = "컨텐츠 요약", example = "대전 당일치기 대표 컨텐츠")
		String contentSummary,

		@Schema(description = "지역명", example = "대전")
		String regionName,

		@Schema(description = "테마명", example = "맛집")
		String themeName,

		@Schema(description = "대표 이미지 URL", example = "https://image.test/thumb.jpg")
		String thumbnailUrl,

		@Schema(description = "컨텐츠 유형", example = "RESTAURANT")
		ContentType contentType,

		@Schema(description = "찜 생성 시각", example = "2026-06-14T10:00:00")
		LocalDateTime createdAt
) {
}
