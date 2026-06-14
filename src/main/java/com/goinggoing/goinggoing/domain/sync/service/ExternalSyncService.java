package com.goinggoing.goinggoing.domain.sync.service;

import com.goinggoing.goinggoing.domain.content.entity.ContentSourceType;
import com.goinggoing.goinggoing.domain.sync.client.ExternalDataSyncClient;
import com.goinggoing.goinggoing.domain.sync.client.ExternalSyncResult;
import com.goinggoing.goinggoing.domain.sync.dto.ExternalSyncLogResponse;
import com.goinggoing.goinggoing.domain.sync.dto.ExternalSyncResponse;
import com.goinggoing.goinggoing.domain.sync.entity.ExternalSyncLog;
import com.goinggoing.goinggoing.domain.sync.repository.ExternalSyncLogRepository;
import com.goinggoing.goinggoing.domain.user.service.AdminAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

@Service
public class ExternalSyncService {

	private static final String CONTENTS_ENDPOINT = "/external/kto/contents";
	private static final String RELATED_PLACES_ENDPOINT = "/external/kto/related-places";
	private static final String REGIONAL_DEMAND_ENDPOINT = "/external/kto/regional-demand";

	private final AdminAuthorizationService adminAuthorizationService;
	private final ExternalDataSyncClient externalDataSyncClient;
	private final ExternalSyncLogRepository externalSyncLogRepository;

	public ExternalSyncService(
			AdminAuthorizationService adminAuthorizationService,
			ExternalDataSyncClient externalDataSyncClient,
			ExternalSyncLogRepository externalSyncLogRepository
	) {
		this.adminAuthorizationService = adminAuthorizationService;
		this.externalDataSyncClient = externalDataSyncClient;
		this.externalSyncLogRepository = externalSyncLogRepository;
	}

	@Transactional
	public ExternalSyncResponse syncContents(Long adminUserId) {
		adminAuthorizationService.validateAdmin(adminUserId);
		return executeSync(ContentSourceType.KTO_TOUR_API, CONTENTS_ENDPOINT, externalDataSyncClient::syncContents);
	}

	@Transactional
	public ExternalSyncResponse syncRelatedPlaces(Long adminUserId) {
		adminAuthorizationService.validateAdmin(adminUserId);
		return executeSync(
				ContentSourceType.KTO_RELATED_ATTRACTION,
				RELATED_PLACES_ENDPOINT,
				externalDataSyncClient::syncRelatedPlaces
		);
	}

	@Transactional
	public ExternalSyncResponse syncRegionalDemand(Long adminUserId) {
		adminAuthorizationService.validateAdmin(adminUserId);
		return executeSync(
				ContentSourceType.KTO_REGIONAL_DEMAND,
				REGIONAL_DEMAND_ENDPOINT,
				externalDataSyncClient::syncRegionalDemand
		);
	}

	@Transactional(readOnly = true)
	public List<ExternalSyncLogResponse> getSyncLogs(Long adminUserId) {
		adminAuthorizationService.validateAdmin(adminUserId);
		return externalSyncLogRepository.findAllByOrderByRequestedAtDesc()
				.stream()
				.map(this::toLogResponse)
				.toList();
	}

	private ExternalSyncResponse executeSync(
			ContentSourceType sourceType,
			String endpoint,
			Supplier<ExternalSyncResult> syncSupplier
	) {
		ExternalSyncLog log;
		try {
			ExternalSyncResult result = syncSupplier.get();
			log = ExternalSyncLog.success(
					sourceType,
					endpoint,
					result.importedCount(),
					result.failedCount(),
					result.message()
			);
		} catch (RuntimeException exception) {
			log = ExternalSyncLog.failed(sourceType, endpoint, exception.getMessage());
		}
		return toResponse(externalSyncLogRepository.save(log));
	}

	private ExternalSyncResponse toResponse(ExternalSyncLog log) {
		return new ExternalSyncResponse(
				log.getId(),
				log.getSourceType(),
				log.getStatus(),
				log.getImportedCount(),
				log.getFailedCount(),
				log.getMessage()
		);
	}

	private ExternalSyncLogResponse toLogResponse(ExternalSyncLog log) {
		return new ExternalSyncLogResponse(
				log.getId(),
				log.getSourceType(),
				log.getEndpoint(),
				log.getStatus(),
				log.getRequestedAt(),
				log.getCompletedAt(),
				log.getImportedCount(),
				log.getFailedCount(),
				log.getMessage()
		);
	}
}
