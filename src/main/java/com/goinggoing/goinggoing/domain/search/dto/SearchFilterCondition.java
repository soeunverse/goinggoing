package com.goinggoing.goinggoing.domain.search.dto;

import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "검색 필터 조건")
public record SearchFilterCondition(
		@Schema(description = "지역 ID", example = "3")
		Long regionId,

		@Schema(description = "테마 ID", example = "1")
		Long themeId,

		@Schema(description = "하위 카테고리 ID", example = "1")
		Long subThemeId,

		@Schema(description = "컨텐츠 유형", example = "RESTAURANT")
		ContentType contentType,

		@Schema(description = "태그 ID 목록", example = "[1, 2]")
		List<Long> tagIds
) {
}
