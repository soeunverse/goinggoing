package com.goinggoing.goinggoing.domain.user.controller;

import com.goinggoing.goinggoing.domain.user.dto.UserProfileResponse;
import com.goinggoing.goinggoing.domain.user.dto.UserProfileUpdateRequest;
import com.goinggoing.goinggoing.domain.user.entity.UserStatus;
import com.goinggoing.goinggoing.domain.user.service.UserProfileService;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import com.goinggoing.goinggoing.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserProfileMutationControllerTest {

	private final UserProfileService userProfileService = mock(UserProfileService.class);
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new UserProfileController(userProfileService))
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();

	@Test
	@DisplayName("내 정보 수정 API 성공 시 200과 수정된 사용자 정보를 반환한다")
	void updateMyProfileSuccess() throws Exception {
		when(userProfileService.updateMyProfile(any(Long.class), any(UserProfileUpdateRequest.class)))
				.thenReturn(new UserProfileResponse(1L, "user@example.com", "새닉네임", UserStatus.ACTIVE));

		mockMvc.perform(patch("/api/users/me")
						.header("X-USER-ID", "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "nickname": "새닉네임"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.userId").value(1L))
				.andExpect(jsonPath("$.data.nickname").value("새닉네임"))
				.andExpect(jsonPath("$.data.status").value("ACTIVE"))
				.andExpect(jsonPath("$.message").value("내 정보 수정이 완료되었습니다."))
				.andExpect(jsonPath("$.errorCode").value(nullValue()));
	}

	@Test
	@DisplayName("내 정보 수정에서 닉네임이 공백이면 400과 INVALID_NICKNAME 응답을 반환한다")
	void invalidNicknameUpdateFails() throws Exception {
		when(userProfileService.updateMyProfile(any(Long.class), any(UserProfileUpdateRequest.class)))
				.thenThrow(new BusinessException(ErrorCode.INVALID_NICKNAME));

		mockMvc.perform(patch("/api/users/me")
						.header("X-USER-ID", "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "nickname": " "
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("INVALID_NICKNAME"));
	}

	@Test
	@DisplayName("내 정보 수정에서 사용자가 없으면 404와 USER_NOT_FOUND 응답을 반환한다")
	void updateMissingUserFails() throws Exception {
		when(userProfileService.updateMyProfile(any(Long.class), any(UserProfileUpdateRequest.class)))
				.thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

		mockMvc.perform(patch("/api/users/me")
						.header("X-USER-ID", "999")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "nickname": "새닉네임"
								}
								"""))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"));
	}

	@Test
	@DisplayName("회원 탈퇴 API 성공 시 200과 데이터 없는 성공 응답을 반환한다")
	void withdrawSuccess() throws Exception {
		mockMvc.perform(delete("/api/users/me")
						.header("X-USER-ID", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data").value(nullValue()))
				.andExpect(jsonPath("$.message").value("회원 탈퇴가 완료되었습니다."))
				.andExpect(jsonPath("$.errorCode").value(nullValue()));

		verify(userProfileService).withdraw(1L);
	}

	@Test
	@DisplayName("회원 탈퇴에서 X-USER-ID 헤더가 없으면 401과 UNAUTHORIZED 응답을 반환한다")
	void missingHeaderWithdrawFails() throws Exception {
		mockMvc.perform(delete("/api/users/me"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
	}
}
