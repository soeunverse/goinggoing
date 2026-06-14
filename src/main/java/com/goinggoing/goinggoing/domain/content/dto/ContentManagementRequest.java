package com.goinggoing.goinggoing.domain.content.dto;

import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "컨텐츠 생성/수정 요청")
public record ContentManagementRequest(
		@Schema(description = "지역 ID", example = "3")
		Long regionId,

		@Schema(description = "테마 ID", example = "1")
		Long themeId,

		@Schema(description = "하위 카테고리 ID", example = "1")
		Long subThemeId,

		@Schema(description = "컨텐츠 제목", example = "성심당")
		String title,

		@Schema(description = "컨텐츠 유형", example = "RESTAURANT")
		ContentType contentType,

		@Schema(description = "요약 설명", example = "대전 당일치기 대표 컨텐츠")
		String summary,

		@Schema(description = "상세 설명", example = "카드뉴스 설명")
		String description,

		@Schema(description = "주소", example = "대전 중구 대종로")
		String address,

		@Schema(description = "위도", example = "36.32750")
		BigDecimal latitude,

		@Schema(description = "경도", example = "127.42720")
		BigDecimal longitude,

		@Schema(description = "대표 이미지 URL", example = "https://image.test/thumb.jpg")
		String thumbnailUrl,

		@Schema(description = "공개 여부", example = "true")
		boolean published,

		@Schema(description = "태그 ID 목록", example = "[1, 2]")
		List<Long> tagIds,

		@Schema(description = "카드뉴스 목록")
		List<ContentManagementCardRequest> cards
) {
}
