package com.goinggoing.goinggoing.domain.sync.service;

import com.goinggoing.goinggoing.domain.content.entity.ContentSourceType;
import com.goinggoing.goinggoing.domain.sync.client.ExternalDataSyncClient;
import com.goinggoing.goinggoing.domain.sync.client.ExternalSyncResult;
import com.goinggoing.goinggoing.domain.sync.dto.ExternalSyncLogResponse;
import com.goinggoing.goinggoing.domain.sync.dto.ExternalSyncResponse;
import com.goinggoing.goinggoing.domain.sync.entity.ExternalSyncLog;
import com.goinggoing.goinggoing.domain.sync.repository.ExternalSyncLogRepository;
import com.goinggoing.goinggoing.domain.user.service.AdminAuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Service
public class ExternalSyncService {

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
	public ExternalSyncResponse syncRelatedPlaces(Long adminUserId) {
		// 관리자 권한 검증
		adminAuthorizationService.validateAdmin(adminUserId);
		// 연관 관광지 동기화 실행
		return executeSync(
				ContentSourceType.KTO_RELATED_ATTRACTION,
				RELATED_PLACES_ENDPOINT,
				externalDataSyncClient::syncRelatedPlaces
		);
	}

	@Transactional
	public ExternalSyncResponse syncRegionalDemand(Long adminUserId) {
		// 관리자 권한 검증
		adminAuthorizationService.validateAdmin(adminUserId);
		// 지역수요 동기화 실행
		return executeSync(
				ContentSourceType.KTO_REGIONAL_DEMAND,
				REGIONAL_DEMAND_ENDPOINT,
				externalDataSyncClient::syncRegionalDemand
		);
	}

	@Transactional(readOnly = true)
	public List<ExternalSyncLogResponse> getSyncLogs(Long adminUserId) {
		// 관리자 권한 검증
		adminAuthorizationService.validateAdmin(adminUserId);
		// 동기화 로그 응답 생성
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
		ExternalSyncLog syncLog;
		try {
			// 외부 client 실행
			ExternalSyncResult result = syncSupplier.get();
			// 성공/부분성공 로그 생성
			syncLog = ExternalSyncLog.success(
					sourceType,
					endpoint,
					result.importedCount(),
					result.failedCount(),
					result.message()
			);
		} catch (RuntimeException exception) {
			// 실패 로그 생성
			syncLog = ExternalSyncLog.failed(sourceType, endpoint, exception.getMessage());
		}
		// 로그 저장 후 응답 생성
		ExternalSyncLog savedLog = externalSyncLogRepository.save(syncLog);
		log.info(
				"[DB 저장] 외부 동기화 로그 저장 syncLogId={} sourceType={} status={} importedCount={} failedCount={}",
				savedLog.getId(),
				savedLog.getSourceType(),
				savedLog.getStatus(),
				savedLog.getImportedCount(),
				savedLog.getFailedCount()
		);
		return toResponse(savedLog);
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
