package com.goinggoing.goinggoing.domain.content.dto;

import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "컨텐츠 상세 응답")
public record ContentDetailResponse(
		@Schema(description = "컨텐츠 ID", example = "1")
		Long id,

		@Schema(description = "컨텐츠 제목", example = "성심당")
		String title,

		@Schema(description = "요약 설명", example = "대전 당일치기 대표 컨텐츠")
		String summary,

		@Schema(description = "상세 설명", example = "카드뉴스로 보기 좋은 상세 설명")
		String description,

		@Schema(description = "주소", example = "대전 중구 대종로480번길 15")
		String address,

		@Schema(description = "위도", example = "36.32750")
		BigDecimal latitude,

		@Schema(description = "경도", example = "127.42720")
		BigDecimal longitude,

		@Schema(description = "대표 이미지 URL", example = "https://image.test/thumb.jpg")
		String thumbnailUrl,

		@Schema(description = "컨텐츠 유형", example = "RESTAURANT")
		ContentType contentType,

		@Schema(description = "지역명", example = "대전")
		String regionName,

		@Schema(description = "테마명", example = "맛집")
		String themeName,

		@Schema(description = "하위 카테고리명", example = "빵지순례")
		String subThemeName,

		@Schema(description = "조회수", example = "121")
		Long viewCount,

		@Schema(description = "찜수", example = "30")
		Long bookmarkCount,

		@Schema(description = "HOT 배지 노출 여부", example = "true")
		boolean hot,

		@Schema(description = "카드뉴스 목록")
		List<ContentCardResponse> cards,

		@Schema(description = "태그 목록")
		List<ContentTagResponse> tags
) {
}
