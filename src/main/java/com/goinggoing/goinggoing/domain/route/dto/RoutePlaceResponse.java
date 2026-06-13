package com.goinggoing.goinggoing.domain.route.dto;

import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "루트 장소 응답")
public record RoutePlaceResponse(
		@Schema(description = "루트 장소 ID", example = "1")
		Long routePlaceId,

		@Schema(description = "연결된 컨텐츠 ID", example = "1")
		Long contentId,

		@Schema(description = "장소명", example = "성심당 본점")
		String name,

		@Schema(description = "장소 유형", example = "RESTAURANT")
		ContentType placeType,

		@Schema(description = "주소", example = "대전 중구 대종로480번길 15")
		String address,

		@Schema(description = "위도", example = "36.32750")
		BigDecimal latitude,

		@Schema(description = "경도", example = "127.42720")
		BigDecimal longitude,

		@Schema(description = "여행 일차", example = "1")
		Integer dayNumber,

		@Schema(description = "방문 순서", example = "1")
		Integer visitOrder,

		@Schema(description = "예상 체류 시간 분", example = "60")
		Integer estimatedStayMinutes,

		@Schema(description = "이전 장소에서 이동 시간 분", example = "10")
		Integer moveMinutesFromPrevious,

		@Schema(description = "추천 메모", example = "대표 빵집에서 여행 시작")
		String recommendationNote
) {
}
