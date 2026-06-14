package com.goinggoing.goinggoing.domain.sync.controller;

import com.goinggoing.goinggoing.domain.content.entity.ContentSourceType;
import com.goinggoing.goinggoing.domain.sync.dto.ExternalSyncLogResponse;
import com.goinggoing.goinggoing.domain.sync.dto.ExternalSyncResponse;
import com.goinggoing.goinggoing.domain.sync.entity.ExternalSyncStatus;
import com.goinggoing.goinggoing.domain.sync.service.ExternalSyncService;
import com.goinggoing.goinggoing.global.exception.GlobalExceptionHandler;
import com.goinggoing.goinggoing.global.security.CurrentUserExtractor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExternalSyncControllerTest {

	private final ExternalSyncService externalSyncService = mock(ExternalSyncService.class);
	private final CurrentUserExtractor currentUserExtractor = mock(CurrentUserExtractor.class);
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new ExternalSyncController(externalSyncService, currentUserExtractor))
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();

	@Test
	@DisplayName("연관 관광지 동기화 API는 200과 동기화 결과를 반환한다")
	void syncRelatedPlacesSuccess() throws Exception {
		when(currentUserExtractor.extractUserId("Bearer admin-token")).thenReturn(1L);
		when(externalSyncService.syncRelatedPlaces(1L)).thenReturn(response(ContentSourceType.KTO_RELATED_ATTRACTION));

		mockMvc.perform(post("/api/admin/sync/related-places")
						.header("Authorization", "Bearer admin-token"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.sourceType").value("KTO_RELATED_ATTRACTION"));
	}

	@Test
	@DisplayName("지역수요 동기화 API는 200과 동기화 결과를 반환한다")
	void syncRegionalDemandSuccess() throws Exception {
		when(currentUserExtractor.extractUserId("Bearer admin-token")).thenReturn(1L);
		when(externalSyncService.syncRegionalDemand(1L)).thenReturn(response(ContentSourceType.KTO_REGIONAL_DEMAND));

		mockMvc.perform(post("/api/admin/sync/regional-demand")
						.header("Authorization", "Bearer admin-token"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.sourceType").value("KTO_REGIONAL_DEMAND"));
	}

	@Test
	@DisplayName("동기화 로그 조회 API는 200과 로그 목록을 반환한다")
	void getSyncLogsSuccess() throws Exception {
		when(currentUserExtractor.extractUserId("Bearer admin-token")).thenReturn(1L);
		when(externalSyncService.getSyncLogs(1L)).thenReturn(List.of(log()));

		mockMvc.perform(get("/api/admin/sync/logs")
						.header("Authorization", "Bearer admin-token"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].logId").value(1L))
				.andExpect(jsonPath("$.message").value("동기화 로그 조회가 완료되었습니다."));
	}

	@Test
	@DisplayName("Authorization 헤더가 없으면 401을 반환한다")
	void missingAuthorizationFails() throws Exception {
		mockMvc.perform(post("/api/admin/sync/related-places"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
	}

	private ExternalSyncResponse response(ContentSourceType sourceType) {
		return new ExternalSyncResponse(1L, sourceType, ExternalSyncStatus.SUCCESS, 2, 0, "완료");
	}

	private ExternalSyncLogResponse log() {
		return new ExternalSyncLogResponse(
				1L,
				ContentSourceType.KTO_RELATED_ATTRACTION,
				"/related-places",
				ExternalSyncStatus.SUCCESS,
				LocalDateTime.of(2026, 6, 14, 10, 0),
				LocalDateTime.of(2026, 6, 14, 10, 1),
				2,
				0,
				"완료"
		);
	}
}
