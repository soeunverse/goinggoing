package com.goinggoing.goinggoing.domain.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 공통 항목 응답")
public record CategoryItemResponse(
		@Schema(description = "항목 ID", example = "1")
		Long id,

		@Schema(description = "항목 이름", example = "대전")
		String name,

		@Schema(description = "화면 노출 순서", example = "1")
		Integer displayOrder
) {
}
