package com.goinggoing.goinggoing.domain.user.controller;

import com.goinggoing.goinggoing.domain.user.dto.UserPreferenceRequest;
import com.goinggoing.goinggoing.domain.user.dto.UserPreferenceResponse;
import com.goinggoing.goinggoing.domain.user.service.UserPreferenceService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me/preferences")
public class UserPreferenceController {

	private final UserPreferenceService userPreferenceService;

	public UserPreferenceController(UserPreferenceService userPreferenceService) {
		this.userPreferenceService = userPreferenceService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<UserPreferenceResponse>> getMyPreference(@RequestHeader("X-USER-ID") Long userId) {
		UserPreferenceResponse response = userPreferenceService.getMyPreference(userId);
		return ResponseEntity.ok(ApiResponse.success(response, "온보딩 취향 조회가 완료되었습니다."));
	}

	@PutMapping
	public ResponseEntity<ApiResponse<UserPreferenceResponse>> saveMyPreference(
			@RequestHeader("X-USER-ID") Long userId,
			@RequestBody UserPreferenceRequest request
	) {
		UserPreferenceResponse response = userPreferenceService.saveMyPreference(userId, request);
		return ResponseEntity.ok(ApiResponse.success(response, "온보딩 취향 저장이 완료되었습니다."));
	}
}
