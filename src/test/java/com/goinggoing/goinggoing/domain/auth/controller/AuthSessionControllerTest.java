package com.goinggoing.goinggoing.domain.auth.controller;

import com.goinggoing.goinggoing.domain.auth.dto.AuthTokenResponse;
import com.goinggoing.goinggoing.domain.auth.dto.LoginRequest;
import com.goinggoing.goinggoing.domain.auth.dto.LogoutRequest;
import com.goinggoing.goinggoing.domain.auth.dto.RefreshTokenRequest;
import com.goinggoing.goinggoing.domain.auth.service.AuthSessionService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthSessionControllerTest {

	private final AuthSessionService authSessionService = mock(AuthSessionService.class);
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new AuthSessionController(authSessionService))
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();

	@Test
	@DisplayName("로그인 API 성공 시 200과 토큰 응답을 반환한다")
	void loginSuccess() throws Exception {
		when(authSessionService.login(any(LoginRequest.class)))
				.thenReturn(new AuthTokenResponse(1L, "access-token", "refresh-token", "Bearer", 3600L));

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "user@example.com",
								  "password": "password123"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.userId").value(1L))
				.andExpect(jsonPath("$.data.accessToken").value("access-token"))
				.andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
				.andExpect(jsonPath("$.data.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.data.accessTokenExpiresInSeconds").value(3600L))
				.andExpect(jsonPath("$.message").value("로그인이 완료되었습니다."))
				.andExpect(jsonPath("$.errorCode").value(nullValue()));
	}

	@Test
	@DisplayName("로그인 실패 시 401과 INVALID_LOGIN_CREDENTIALS 응답을 반환한다")
	void loginFailure() throws Exception {
		when(authSessionService.login(any(LoginRequest.class)))
				.thenThrow(new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS));

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "user@example.com",
								  "password": "wrong1234"
								}
								"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("INVALID_LOGIN_CREDENTIALS"));
	}

	@Test
	@DisplayName("토큰 갱신 API 성공 시 200과 새 토큰 응답을 반환한다")
	void refreshSuccess() throws Exception {
		when(authSessionService.refresh(any(RefreshTokenRequest.class)))
				.thenReturn(new AuthTokenResponse(1L, "new-access", "new-refresh", "Bearer", 3600L));

		mockMvc.perform(post("/api/auth/refresh")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "refreshToken": "refresh-token"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.accessToken").value("new-access"))
				.andExpect(jsonPath("$.data.refreshToken").value("new-refresh"))
				.andExpect(jsonPath("$.message").value("토큰이 갱신되었습니다."));
	}

	@Test
	@DisplayName("토큰 갱신 실패 시 401과 INVALID_REFRESH_TOKEN 응답을 반환한다")
	void refreshFailure() throws Exception {
		when(authSessionService.refresh(any(RefreshTokenRequest.class)))
				.thenThrow(new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

		mockMvc.perform(post("/api/auth/refresh")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "refreshToken": "invalid-refresh"
								}
								"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("INVALID_REFRESH_TOKEN"));
	}

	@Test
	@DisplayName("로그아웃 API 성공 시 200과 데이터 없는 성공 응답을 반환한다")
	void logoutSuccess() throws Exception {
		mockMvc.perform(post("/api/auth/logout")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "refreshToken": "refresh-token"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data").value(nullValue()))
				.andExpect(jsonPath("$.message").value("로그아웃이 완료되었습니다."))
				.andExpect(jsonPath("$.errorCode").value(nullValue()));

		verify(authSessionService).logout(any(LogoutRequest.class));
	}
}
