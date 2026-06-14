package com.goinggoing.goinggoing.domain.content.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "컨텐츠 카드뉴스 관리 요청")
public record ContentManagementCardRequest(
		@Schema(description = "카드 제목", example = "대표 메뉴")
		String title,

		@Schema(description = "카드 본문", example = "튀김소보로와 부추빵")
		String body,

		@Schema(description = "카드 이미지 URL", example = "https://image.test/card.jpg")
		String imageUrl,

		@Schema(description = "카드 노출 순서", example = "1")
		Integer displayOrder
) {
}
