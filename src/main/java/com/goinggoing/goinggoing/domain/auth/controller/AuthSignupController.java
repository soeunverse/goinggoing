package com.goinggoing.goinggoing.domain.auth.controller;

import com.goinggoing.goinggoing.domain.user.dto.UserSignupRequest;
import com.goinggoing.goinggoing.domain.user.dto.UserSignupResponse;
import com.goinggoing.goinggoing.domain.user.service.UserSignupService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthSignupController {

	private final UserSignupService userSignupService;

	public AuthSignupController(UserSignupService userSignupService) {
		this.userSignupService = userSignupService;
	}

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<UserSignupResponse>> signup(@RequestBody UserSignupRequest request) {
		UserSignupResponse response = userSignupService.signup(request);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(ApiResponse.success(response, "회원가입이 완료되었습니다."));
	}
}
