package com.goinggoing.goinggoing.domain.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "테마 응답")
public record ThemeResponse(
		@Schema(description = "테마 ID", example = "1")
		Long id,

		@Schema(description = "테마 이름", example = "맛집")
		String name,

		@Schema(description = "테마 설명", example = "먹으러 떠나는 당일치기 여행")
		String description,

		@Schema(description = "화면 노출 순서", example = "1")
		Integer displayOrder,

		@Schema(description = "연결된 컨텐츠 수. MVP 초기에는 0으로 응답", example = "0")
		Long contentCount
) {
}
