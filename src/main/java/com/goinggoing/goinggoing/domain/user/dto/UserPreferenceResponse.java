package com.goinggoing.goinggoing.domain.user.dto;

import com.goinggoing.goinggoing.domain.user.entity.TripDurationType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "온보딩 취향 응답")
public record UserPreferenceResponse(
		@Schema(description = "선호 여행 기간", example = "DAY_TRIP")
		TripDurationType preferredTripDuration,
		@Schema(description = "선호 지역 ID 목록", example = "[1, 2]")
		List<Long> regionIds,
		@Schema(description = "선호 테마 ID 목록", example = "[10, 11]")
		List<Long> themeIds,
		@Schema(description = "선호 태그 ID 목록", example = "[100, 101]")
		List<Long> tagIds
) {

	public static UserPreferenceResponse empty() {
		return new UserPreferenceResponse(null, List.of(), List.of(), List.of());
	}
}
