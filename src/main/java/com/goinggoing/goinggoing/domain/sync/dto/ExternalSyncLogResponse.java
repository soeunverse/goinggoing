package com.goinggoing.goinggoing.domain.sync.dto;

import com.goinggoing.goinggoing.domain.content.entity.ContentSourceType;
import com.goinggoing.goinggoing.domain.sync.entity.ExternalSyncStatus;

import java.time.LocalDateTime;

public record ExternalSyncLogResponse(
		Long logId,
		ContentSourceType sourceType,
		String endpoint,
		ExternalSyncStatus status,
		LocalDateTime requestedAt,
		LocalDateTime completedAt,
		Integer importedCount,
		Integer failedCount,
		String message
) {
}
