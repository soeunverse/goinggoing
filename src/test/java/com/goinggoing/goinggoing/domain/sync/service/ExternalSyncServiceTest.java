package com.goinggoing.goinggoing.domain.sync.service;

import com.goinggoing.goinggoing.domain.content.entity.ContentSourceType;
import com.goinggoing.goinggoing.domain.sync.client.ExternalDataSyncClient;
import com.goinggoing.goinggoing.domain.sync.client.ExternalSyncResult;
import com.goinggoing.goinggoing.domain.sync.dto.ExternalSyncLogResponse;
import com.goinggoing.goinggoing.domain.sync.dto.ExternalSyncResponse;
import com.goinggoing.goinggoing.domain.sync.entity.ExternalSyncLog;
import com.goinggoing.goinggoing.domain.sync.entity.ExternalSyncStatus;
import com.goinggoing.goinggoing.domain.sync.repository.ExternalSyncLogRepository;
import com.goinggoing.goinggoing.domain.user.service.AdminAuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExternalSyncServiceTest {

	private AdminAuthorizationService adminAuthorizationService;
	private ExternalDataSyncClient externalDataSyncClient;
	private ExternalSyncLogRepository externalSyncLogRepository;
	private ExternalSyncService externalSyncService;

	@BeforeEach
	void setUp() {
		adminAuthorizationService = mock(AdminAuthorizationService.class);
		externalDataSyncClient = mock(ExternalDataSyncClient.class);
		externalSyncLogRepository = mock(ExternalSyncLogRepository.class);
		externalSyncService = new ExternalSyncService(
				adminAuthorizationService,
				externalDataSyncClient,
				externalSyncLogRepository
		);
	}

	@Test
	@DisplayName("ADMIN 사용자는 컨텐츠 동기화를 실행하고 SUCCESS 로그 응답을 받는다")
	void syncContentsSuccess() {
		doNothing().when(adminAuthorizationService).validateAdmin(1L);
		when(externalDataSyncClient.syncContents()).thenReturn(new ExternalSyncResult(2, 0, "컨텐츠 동기화 완료"));
		when(externalSyncLogRepository.save(any(ExternalSyncLog.class)))
				.thenAnswer(invocation -> ((ExternalSyncLog) invocation.getArgument(0)).withId(1L));

		ExternalSyncResponse response = externalSyncService.syncContents(1L);

		assertThat(response.logId()).isEqualTo(1L);
		assertThat(response.sourceType()).isEqualTo(ContentSourceType.KTO_TOUR_API);
		assertThat(response.status()).isEqualTo(ExternalSyncStatus.SUCCESS);
		assertThat(response.importedCount()).isEqualTo(2);
		verify(adminAuthorizationService).validateAdmin(1L);
	}

	@Test
	@DisplayName("외부 동기화 client 실패는 FAILED 로그로 저장한다")
	void syncContentsClientFailureLogsFailed() {
		doNothing().when(adminAuthorizationService).validateAdmin(1L);
		when(externalDataSyncClient.syncContents()).thenThrow(new IllegalStateException("API 실패"));
		when(externalSyncLogRepository.save(any(ExternalSyncLog.class)))
				.thenAnswer(invocation -> ((ExternalSyncLog) invocation.getArgument(0)).withId(2L));

		ExternalSyncResponse response = externalSyncService.syncContents(1L);

		assertThat(response.logId()).isEqualTo(2L);
		assertThat(response.status()).isEqualTo(ExternalSyncStatus.FAILED);
		assertThat(response.failedCount()).isEqualTo(1);
		assertThat(response.message()).contains("API 실패");
	}

	@Test
	@DisplayName("ADMIN 사용자는 연관 관광지 동기화를 실행한다")
	void syncRelatedPlacesSuccess() {
		doNothing().when(adminAuthorizationService).validateAdmin(1L);
		when(externalDataSyncClient.syncRelatedPlaces()).thenReturn(new ExternalSyncResult(3, 0, "연관 관광지 동기화 완료"));
		when(externalSyncLogRepository.save(any(ExternalSyncLog.class)))
				.thenAnswer(invocation -> ((ExternalSyncLog) invocation.getArgument(0)).withId(3L));

		ExternalSyncResponse response = externalSyncService.syncRelatedPlaces(1L);

		assertThat(response.sourceType()).isEqualTo(ContentSourceType.KTO_RELATED_ATTRACTION);
		assertThat(response.importedCount()).isEqualTo(3);
	}

	@Test
	@DisplayName("ADMIN 사용자는 지역수요 동기화를 실행한다")
	void syncRegionalDemandSuccess() {
		doNothing().when(adminAuthorizationService).validateAdmin(1L);
		when(externalDataSyncClient.syncRegionalDemand()).thenReturn(new ExternalSyncResult(4, 0, "지역수요 동기화 완료"));
		when(externalSyncLogRepository.save(any(ExternalSyncLog.class)))
				.thenAnswer(invocation -> ((ExternalSyncLog) invocation.getArgument(0)).withId(4L));

		ExternalSyncResponse response = externalSyncService.syncRegionalDemand(1L);

		assertThat(response.sourceType()).isEqualTo(ContentSourceType.KTO_REGIONAL_DEMAND);
		assertThat(response.importedCount()).isEqualTo(4);
	}

	@Test
	@DisplayName("ADMIN 사용자는 외부 동기화 로그를 조회한다")
	void getSyncLogsSuccess() {
		doNothing().when(adminAuthorizationService).validateAdmin(1L);
		when(externalSyncLogRepository.findAllByOrderByRequestedAtDesc())
				.thenReturn(List.of(ExternalSyncLog.success(ContentSourceType.KTO_TOUR_API, "/contents", 2, 0, "완료").withId(1L)));

		List<ExternalSyncLogResponse> responses = externalSyncService.getSyncLogs(1L);

		assertThat(responses).extracting(ExternalSyncLogResponse::logId).containsExactly(1L);
		assertThat(responses.get(0).status()).isEqualTo(ExternalSyncStatus.SUCCESS);
	}
}
