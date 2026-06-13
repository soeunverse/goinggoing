package com.goinggoing.goinggoing.domain.route.dto;

import com.goinggoing.goinggoing.domain.user.entity.TripDurationType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "루트 생성 요청")
public record RouteGenerateRequest(
		@Schema(description = "기준 컨텐츠 ID", example = "1")
		Long contentId,

		@Schema(description = "여행 기간", example = "DAY_TRIP")
		TripDurationType tripDurationType
) {
}
