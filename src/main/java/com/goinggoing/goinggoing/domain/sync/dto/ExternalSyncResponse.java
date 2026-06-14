package com.goinggoing.goinggoing.domain.sync.dto;

import com.goinggoing.goinggoing.domain.content.entity.ContentSourceType;
import com.goinggoing.goinggoing.domain.sync.entity.ExternalSyncStatus;

public record ExternalSyncResponse(
		Long logId,
		ContentSourceType sourceType,
		ExternalSyncStatus status,
		Integer importedCount,
		Integer failedCount,
		String message
) {
}
