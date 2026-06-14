package com.goinggoing.goinggoing.domain.sync.controller;

import com.goinggoing.goinggoing.domain.sync.dto.ExternalSyncLogResponse;
import com.goinggoing.goinggoing.domain.sync.dto.ExternalSyncResponse;
import com.goinggoing.goinggoing.domain.sync.service.ExternalSyncService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import com.goinggoing.goinggoing.global.security.CurrentUserExtractor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sync")
public class ExternalSyncController {

	private final ExternalSyncService externalSyncService;
	private final CurrentUserExtractor currentUserExtractor;

	public ExternalSyncController(ExternalSyncService externalSyncService, CurrentUserExtractor currentUserExtractor) {
		this.externalSyncService = externalSyncService;
		this.currentUserExtractor = currentUserExtractor;
	}

	@PostMapping("/contents")
	public ResponseEntity<ApiResponse<ExternalSyncResponse>> syncContents(
			@RequestHeader("Authorization") String authorizationHeader
	) {
		Long adminUserId = currentUserExtractor.extractUserId(authorizationHeader);
		ExternalSyncResponse response = externalSyncService.syncContents(adminUserId);
		return ResponseEntity.ok(ApiResponse.success(response, "컨텐츠 동기화가 완료되었습니다."));
	}

	@PostMapping("/related-places")
	public ResponseEntity<ApiResponse<ExternalSyncResponse>> syncRelatedPlaces(
			@RequestHeader("Authorization") String authorizationHeader
	) {
		Long adminUserId = currentUserExtractor.extractUserId(authorizationHeader);
		ExternalSyncResponse response = externalSyncService.syncRelatedPlaces(adminUserId);
		return ResponseEntity.ok(ApiResponse.success(response, "연관 관광지 동기화가 완료되었습니다."));
	}

	@PostMapping("/regional-demand")
	public ResponseEntity<ApiResponse<ExternalSyncResponse>> syncRegionalDemand(
			@RequestHeader("Authorization") String authorizationHeader
	) {
		Long adminUserId = currentUserExtractor.extractUserId(authorizationHeader);
		ExternalSyncResponse response = externalSyncService.syncRegionalDemand(adminUserId);
		return ResponseEntity.ok(ApiResponse.success(response, "지역수요 동기화가 완료되었습니다."));
	}

	@GetMapping("/logs")
	public ResponseEntity<ApiResponse<List<ExternalSyncLogResponse>>> getSyncLogs(
			@RequestHeader("Authorization") String authorizationHeader
	) {
		Long adminUserId = currentUserExtractor.extractUserId(authorizationHeader);
		List<ExternalSyncLogResponse> response = externalSyncService.getSyncLogs(adminUserId);
		return ResponseEntity.ok(ApiResponse.success(response, "동기화 로그 조회가 완료되었습니다."));
	}
}
