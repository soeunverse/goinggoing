package com.goinggoing.goinggoing.domain.user.controller;

import com.goinggoing.goinggoing.domain.user.dto.UserPreferenceRequest;
import com.goinggoing.goinggoing.domain.user.dto.UserPreferenceResponse;
import com.goinggoing.goinggoing.domain.user.entity.TripDurationType;
import com.goinggoing.goinggoing.domain.user.service.UserPreferenceService;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import com.goinggoing.goinggoing.global.exception.GlobalExceptionHandler;
import com.goinggoing.goinggoing.global.security.CurrentUserExtractor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserPreferenceControllerTest {

	private final UserPreferenceService userPreferenceService = mock(UserPreferenceService.class);
	private final CurrentUserExtractor currentUserExtractor = mock(CurrentUserExtractor.class);
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new UserPreferenceController(userPreferenceService, currentUserExtractor))
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();

	@Test
	@DisplayName("온보딩 취향 조회 API 성공 시 200과 취향 정보를 반환한다")
	void getMyPreferenceSuccess() throws Exception {
		when(userPreferenceService.getMyPreference(1L))
				.thenReturn(new UserPreferenceResponse(
						TripDurationType.DAY_TRIP,
						List.of(1L, 2L),
						List.of(10L),
						List.of(100L)
				));
		when(currentUserExtractor.extractUserId("Bearer access-token")).thenReturn(1L);

		mockMvc.perform(get("/api/users/me/preferences")
						.header("Authorization", "Bearer access-token"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.preferredTripDuration").value("DAY_TRIP"))
				.andExpect(jsonPath("$.data.regionIds[0]").value(1L))
				.andExpect(jsonPath("$.data.themeIds[0]").value(10L))
				.andExpect(jsonPath("$.data.tagIds[0]").value(100L))
				.andExpect(jsonPath("$.message").value("온보딩 취향 조회가 완료되었습니다."))
				.andExpect(jsonPath("$.errorCode").value(nullValue()));
	}

	@Test
	@DisplayName("온보딩 취향 저장 API 성공 시 200과 저장된 취향 정보를 반환한다")
	void saveMyPreferenceSuccess() throws Exception {
		when(userPreferenceService.saveMyPreference(any(Long.class), any(UserPreferenceRequest.class)))
				.thenReturn(new UserPreferenceResponse(
						TripDurationType.ONE_NIGHT_TWO_DAYS,
						List.of(3L),
						List.of(11L, 12L),
						List.of()
				));
		when(currentUserExtractor.extractUserId("Bearer access-token")).thenReturn(1L);

		mockMvc.perform(put("/api/users/me/preferences")
						.header("Authorization", "Bearer access-token")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "preferredTripDuration": "ONE_NIGHT_TWO_DAYS",
								  "regionIds": [3],
								  "themeIds": [11, 12],
								  "tagIds": []
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.preferredTripDuration").value("ONE_NIGHT_TWO_DAYS"))
				.andExpect(jsonPath("$.data.regionIds[0]").value(3L))
				.andExpect(jsonPath("$.data.themeIds[1]").value(12L))
				.andExpect(jsonPath("$.data.tagIds").isEmpty())
				.andExpect(jsonPath("$.message").value("온보딩 취향 저장이 완료되었습니다."));
	}

	@Test
	@DisplayName("온보딩 취향 저장에서 요청이 비어 있으면 400과 INVALID_PREFERENCE 응답을 반환한다")
	void saveEmptyPreferenceFails() throws Exception {
		when(userPreferenceService.saveMyPreference(any(Long.class), any(UserPreferenceRequest.class)))
				.thenThrow(new BusinessException(ErrorCode.INVALID_PREFERENCE));
		when(currentUserExtractor.extractUserId("Bearer access-token")).thenReturn(1L);

		mockMvc.perform(put("/api/users/me/preferences")
						.header("Authorization", "Bearer access-token")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "preferredTripDuration": null,
								  "regionIds": [],
								  "themeIds": [],
								  "tagIds": []
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("INVALID_PREFERENCE"));
	}

	@Test
	@DisplayName("온보딩 취향 조회에서 사용자가 없으면 404와 USER_NOT_FOUND 응답을 반환한다")
	void getMissingUserFails() throws Exception {
		when(userPreferenceService.getMyPreference(999L))
				.thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));
		when(currentUserExtractor.extractUserId("Bearer missing-user-token")).thenReturn(999L);

		mockMvc.perform(get("/api/users/me/preferences")
						.header("Authorization", "Bearer missing-user-token"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"));
	}

	@Test
	@DisplayName("Authorization 헤더가 없으면 401과 UNAUTHORIZED 응답을 반환한다")
	void missingUserIdHeaderFails() throws Exception {
		mockMvc.perform(get("/api/users/me/preferences"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
	}
}
