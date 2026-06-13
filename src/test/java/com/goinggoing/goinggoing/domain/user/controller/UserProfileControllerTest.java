package com.goinggoing.goinggoing.domain.user.controller;

import com.goinggoing.goinggoing.domain.user.dto.UserProfileResponse;
import com.goinggoing.goinggoing.domain.user.entity.UserStatus;
import com.goinggoing.goinggoing.domain.user.service.UserProfileService;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import com.goinggoing.goinggoing.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserProfileControllerTest {

	private final UserProfileService userProfileService = mock(UserProfileService.class);
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new UserProfileController(userProfileService))
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();

	@Test
	@DisplayName("내 정보 조회 API 성공 시 200과 사용자 정보를 반환한다")
	void getMyProfileSuccess() throws Exception {
		when(userProfileService.getMyProfile(1L))
				.thenReturn(new UserProfileResponse(1L, "user@example.com", "즉흥여행자", UserStatus.ACTIVE));

		mockMvc.perform(get("/api/users/me")
						.header("X-USER-ID", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.userId").value(1L))
				.andExpect(jsonPath("$.data.email").value("user@example.com"))
				.andExpect(jsonPath("$.data.nickname").value("즉흥여행자"))
				.andExpect(jsonPath("$.data.status").value("ACTIVE"))
				.andExpect(jsonPath("$.message").value("내 정보 조회가 완료되었습니다."))
				.andExpect(jsonPath("$.errorCode").value(nullValue()));
	}

	@Test
	@DisplayName("X-USER-ID 헤더가 없으면 401과 UNAUTHORIZED 응답을 반환한다")
	void missingUserIdHeaderFails() throws Exception {
		mockMvc.perform(get("/api/users/me"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
	}

	@Test
	@DisplayName("사용자가 없으면 404와 USER_NOT_FOUND 응답을 반환한다")
	void userNotFoundFails() throws Exception {
		when(userProfileService.getMyProfile(999L))
				.thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

		mockMvc.perform(get("/api/users/me")
						.header("X-USER-ID", "999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"));
	}
}
