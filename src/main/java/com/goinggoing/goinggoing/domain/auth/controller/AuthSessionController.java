package com.goinggoing.goinggoing.domain.auth.controller;

import com.goinggoing.goinggoing.domain.auth.dto.AuthTokenResponse;
import com.goinggoing.goinggoing.domain.auth.dto.LoginRequest;
import com.goinggoing.goinggoing.domain.auth.dto.LogoutRequest;
import com.goinggoing.goinggoing.domain.auth.dto.RefreshTokenRequest;
import com.goinggoing.goinggoing.domain.auth.service.AuthSessionService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthSessionController {

	private final AuthSessionService authSessionService;

	public AuthSessionController(AuthSessionService authSessionService) {
		this.authSessionService = authSessionService;
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthTokenResponse>> login(@RequestBody LoginRequest request) {
		AuthTokenResponse response = authSessionService.login(request);
		return ResponseEntity.ok(ApiResponse.success(response, "로그인이 완료되었습니다."));
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<AuthTokenResponse>> refresh(@RequestBody RefreshTokenRequest request) {
		AuthTokenResponse response = authSessionService.refresh(request);
		return ResponseEntity.ok(ApiResponse.success(response, "토큰이 갱신되었습니다."));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequest request) {
		authSessionService.logout(request);
		return ResponseEntity.ok(ApiResponse.success(null, "로그아웃이 완료되었습니다."));
	}
}
