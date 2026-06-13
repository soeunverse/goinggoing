package com.goinggoing.goinggoing.domain.content.controller;

import com.goinggoing.goinggoing.domain.content.dto.ContentDetailResponse;
import com.goinggoing.goinggoing.domain.content.dto.ContentManagementRequest;
import com.goinggoing.goinggoing.domain.content.service.ContentManagementService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import com.goinggoing.goinggoing.global.security.CurrentUserExtractor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contents")
public class ContentManagementController {

	private final ContentManagementService contentManagementService;
	private final CurrentUserExtractor currentUserExtractor;

	public ContentManagementController(
			ContentManagementService contentManagementService,
			CurrentUserExtractor currentUserExtractor
	) {
		this.contentManagementService = contentManagementService;
		this.currentUserExtractor = currentUserExtractor;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<ContentDetailResponse>> createContent(
			@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody ContentManagementRequest request
	) {
		Long adminUserId = currentUserExtractor.extractUserId(authorizationHeader);
		ContentDetailResponse response = contentManagementService.createContent(adminUserId, request);
		return ResponseEntity.ok(ApiResponse.success(response, "컨텐츠 생성이 완료되었습니다."));
	}

	@PutMapping("/{contentId}")
	public ResponseEntity<ApiResponse<ContentDetailResponse>> updateContent(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Long contentId,
			@RequestBody ContentManagementRequest request
	) {
		Long adminUserId = currentUserExtractor.extractUserId(authorizationHeader);
		ContentDetailResponse response = contentManagementService.updateContent(adminUserId, contentId, request);
		return ResponseEntity.ok(ApiResponse.success(response, "컨텐츠 수정이 완료되었습니다."));
	}

	@DeleteMapping("/{contentId}")
	public ResponseEntity<ApiResponse<Void>> deleteContent(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Long contentId
	) {
		Long adminUserId = currentUserExtractor.extractUserId(authorizationHeader);
		contentManagementService.deleteContent(adminUserId, contentId);
		return ResponseEntity.ok(ApiResponse.success(null, "컨텐츠 삭제가 완료되었습니다."));
	}
}
