package com.goinggoing.goinggoing.domain.content.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "컨텐츠 태그 응답")
public record ContentTagResponse(
		@Schema(description = "태그 ID", example = "1")
		Long id,

		@Schema(description = "태그명", example = "빵지순례")
		String name
) {
}
