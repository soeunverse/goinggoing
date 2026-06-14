package com.goinggoing.goinggoing.domain.sync.client;

public record ExternalSyncResult(
		Integer importedCount,
		Integer failedCount,
		String message
) {
}
