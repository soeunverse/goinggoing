package com.goinggoing.goinggoing.domain.sync.dto;

import com.goinggoing.goinggoing.domain.content.entity.ContentSourceType;
import com.goinggoing.goinggoing.domain.sync.entity.ExternalSyncStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외부 데이터 동기화 실행 응답")
public record ExternalSyncResponse(
		@Schema(description = "동기화 로그 ID", example = "1")
		Long logId,

		@Schema(description = "동기화 소스 유형", example = "KTO_TOUR_API")
		ContentSourceType sourceType,

		@Schema(description = "동기화 상태", example = "SUCCESS")
		ExternalSyncStatus status,

		@Schema(description = "저장/갱신 성공 건수", example = "2")
		Integer importedCount,

		@Schema(description = "실패 건수", example = "0")
		Integer failedCount,

		@Schema(description = "동기화 메시지", example = "완료")
		String message
) {
}
