package com.goinggoing.goinggoing.domain.content.dto;

import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "컨텐츠 카드 응답")
public record ContentSummaryResponse(
		@Schema(description = "컨텐츠 ID", example = "1")
		Long id,

		@Schema(description = "컨텐츠 제목", example = "성심당")
		String title,

		@Schema(description = "카드 목록용 요약", example = "대전 당일치기의 시작점으로 잡기 좋은 대표 빵집")
		String summary,

		@Schema(description = "지역명", example = "대전")
		String regionName,

		@Schema(description = "테마명", example = "맛집")
		String themeName,

		@Schema(description = "대표 이미지 URL", example = "https://image.test/thumb.jpg")
		String thumbnailUrl,

		@Schema(description = "컨텐츠 유형", example = "RESTAURANT")
		ContentType contentType,

		@Schema(description = "조회수", example = "120")
		Long viewCount,

		@Schema(description = "찜수", example = "30")
		Long bookmarkCount,

		@Schema(description = "HOT 배지 노출 여부", example = "true")
		boolean hot
) {
}
