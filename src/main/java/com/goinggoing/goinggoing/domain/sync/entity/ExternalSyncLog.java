package com.goinggoing.goinggoing.domain.sync.entity;

import com.goinggoing.goinggoing.domain.content.entity.ContentSourceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "external_sync_logs")
public class ExternalSyncLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "source_type", nullable = false)
	private ContentSourceType sourceType;

	@Column(nullable = false)
	private String endpoint;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ExternalSyncStatus status;

	@Column(name = "requested_at", nullable = false)
	private LocalDateTime requestedAt;

	@Column(name = "completed_at")
	private LocalDateTime completedAt;

	@Column(name = "imported_count", nullable = false)
	private Integer importedCount;

	@Column(name = "failed_count", nullable = false)
	private Integer failedCount;

	@Column(columnDefinition = "TEXT")
	private String message;

	protected ExternalSyncLog() {
	}

	private ExternalSyncLog(
			Long id,
			ContentSourceType sourceType,
			String endpoint,
			ExternalSyncStatus status,
			LocalDateTime requestedAt,
			LocalDateTime completedAt,
			Integer importedCount,
			Integer failedCount,
			String message
	) {
		this.id = id;
		this.sourceType = sourceType;
		this.endpoint = endpoint;
		this.status = status;
		this.requestedAt = requestedAt;
		this.completedAt = completedAt;
		this.importedCount = importedCount;
		this.failedCount = failedCount;
		this.message = message;
	}

	public static ExternalSyncLog success(
			ContentSourceType sourceType,
			String endpoint,
			Integer importedCount,
			Integer failedCount,
			String message
	) {
		ExternalSyncStatus status = failedCount == 0 ? ExternalSyncStatus.SUCCESS : ExternalSyncStatus.PARTIAL;
		LocalDateTime now = LocalDateTime.now();
		return new ExternalSyncLog(null, sourceType, endpoint, status, now, now, importedCount, failedCount, message);
	}

	public static ExternalSyncLog failed(ContentSourceType sourceType, String endpoint, String message) {
		LocalDateTime now = LocalDateTime.now();
		return new ExternalSyncLog(null, sourceType, endpoint, ExternalSyncStatus.FAILED, now, now, 0, 1, message);
	}

	public ExternalSyncLog withId(Long id) {
		return new ExternalSyncLog(id, sourceType, endpoint, status, requestedAt, completedAt, importedCount, failedCount, message);
	}

	public Long getId() {
		return id;
	}

	public ContentSourceType getSourceType() {
		return sourceType;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public ExternalSyncStatus getStatus() {
		return status;
	}

	public LocalDateTime getRequestedAt() {
		return requestedAt;
	}

	public LocalDateTime getCompletedAt() {
		return completedAt;
	}

	public Integer getImportedCount() {
		return importedCount;
	}

	public Integer getFailedCount() {
		return failedCount;
	}

	public String getMessage() {
		return message;
	}
}
