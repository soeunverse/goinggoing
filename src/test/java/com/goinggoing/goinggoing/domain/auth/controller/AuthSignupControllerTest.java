package com.goinggoing.goinggoing.domain.auth.controller;

import com.goinggoing.goinggoing.domain.user.dto.UserSignupRequest;
import com.goinggoing.goinggoing.domain.user.dto.UserSignupResponse;
import com.goinggoing.goinggoing.domain.user.service.UserSignupService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthSignupControllerTest {

	private final UserSignupService userSignupService = mock(UserSignupService.class);
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new AuthSignupController(userSignupService))
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();

	@Test
	@DisplayName("회원가입 API 성공 시 201과 공통 성공 응답을 반환한다")
	void signupSuccess() throws Exception {
		UserSignupRequest request = new UserSignupRequest("user@example.com", "password123", "즉흥여행자");
		when(userSignupService.signup(any(UserSignupRequest.class)))
				.thenReturn(new UserSignupResponse(1L, "user@example.com", "즉흥여행자"));

		mockMvc.perform(post("/api/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "user@example.com",
								  "password": "password123",
								  "nickname": "즉흥여행자"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.userId").value(1L))
				.andExpect(jsonPath("$.data.email").value("user@example.com"))
				.andExpect(jsonPath("$.data.nickname").value("즉흥여행자"))
				.andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
				.andExpect(jsonPath("$.errorCode").value(nullValue()));
	}

	@Test
	@DisplayName("이메일 중복이면 409와 EMAIL_ALREADY_EXISTS 응답을 반환한다")
	void duplicateEmailFails() throws Exception {
		when(userSignupService.signup(any(UserSignupRequest.class)))
				.thenThrow(new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS));

		mockMvc.perform(post("/api/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "user@example.com",
								  "password": "password123",
								  "nickname": "즉흥여행자"
								}
								"""))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.data").value(nullValue()))
				.andExpect(jsonPath("$.message").value("이미 가입된 이메일입니다."))
				.andExpect(jsonPath("$.errorCode").value("EMAIL_ALREADY_EXISTS"));
	}

	@Test
	@DisplayName("비밀번호 형식 오류면 400과 INVALID_PASSWORD 응답을 반환한다")
	void invalidPasswordFails() throws Exception {
		when(userSignupService.signup(any(UserSignupRequest.class)))
				.thenThrow(new BusinessException(ErrorCode.INVALID_PASSWORD));

		mockMvc.perform(post("/api/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "user@example.com",
								  "password": "short",
								  "nickname": "즉흥여행자"
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("INVALID_PASSWORD"));
	}

	@Test
	@DisplayName("닉네임 형식 오류면 400과 INVALID_NICKNAME 응답을 반환한다")
	void invalidNicknameFails() throws Exception {
		when(userSignupService.signup(any(UserSignupRequest.class)))
				.thenThrow(new BusinessException(ErrorCode.INVALID_NICKNAME));

		mockMvc.perform(post("/api/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "user@example.com",
								  "password": "password123",
								  "nickname": " "
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("INVALID_NICKNAME"));
	}
}
