package com.goinggoing.goinggoing.domain.route.dto;

import com.goinggoing.goinggoing.domain.user.entity.TripDurationType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "루트 응답")
public record RouteResponse(
		@Schema(description = "루트 ID", example = "1")
		Long routeId,

		@Schema(description = "기준 컨텐츠 ID", example = "1")
		Long contentId,

		@Schema(description = "루트 제목", example = "성심당 당일치기 루트")
		String title,

		@Schema(description = "루트 요약", example = "대전 원도심을 가볍게 도는 루트")
		String summary,

		@Schema(description = "여행 기간", example = "DAY_TRIP")
		TripDurationType tripDurationType,

		@Schema(description = "총 이동 거리 미터", example = "3200")
		Integer totalDistanceMeters,

		@Schema(description = "총 소요 시간 분", example = "180")
		Integer totalDurationMinutes,

		@Schema(description = "지도 중심 위도", example = "36.32750")
		BigDecimal mapCenterLatitude,

		@Schema(description = "지도 중심 경도", example = "127.42720")
		BigDecimal mapCenterLongitude,

		@Schema(description = "루트 장소 목록")
		List<RoutePlaceResponse> places
) {
}
