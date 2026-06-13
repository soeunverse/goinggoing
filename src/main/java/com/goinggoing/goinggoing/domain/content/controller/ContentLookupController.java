package com.goinggoing.goinggoing.domain.content.controller;

import com.goinggoing.goinggoing.domain.content.dto.ContentDetailResponse;
import com.goinggoing.goinggoing.domain.content.dto.ContentSummaryResponse;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.content.service.ContentLookupService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contents")
public class ContentLookupController {

	private final ContentLookupService contentLookupService;

	public ContentLookupController(ContentLookupService contentLookupService) {
		this.contentLookupService = contentLookupService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<ContentSummaryResponse>>> getContents(
			@RequestParam(required = false) Long regionId,
			@RequestParam(required = false) Long themeId,
			@RequestParam(required = false) ContentType contentType
	) {
		List<ContentSummaryResponse> response = contentLookupService.getContents(regionId, themeId, contentType);
		return ResponseEntity.ok(ApiResponse.success(response, "컨텐츠 목록 조회가 완료되었습니다."));
	}

	@GetMapping("/hot")
	public ResponseEntity<ApiResponse<List<ContentSummaryResponse>>> getHotContents() {
		List<ContentSummaryResponse> response = contentLookupService.getHotContents();
		return ResponseEntity.ok(ApiResponse.success(response, "HOT 컨텐츠 조회가 완료되었습니다."));
	}

	@GetMapping("/{contentId}")
	public ResponseEntity<ApiResponse<ContentDetailResponse>> getContentDetail(@PathVariable Long contentId) {
		ContentDetailResponse response = contentLookupService.getContentDetail(contentId);
		return ResponseEntity.ok(ApiResponse.success(response, "컨텐츠 상세 조회가 완료되었습니다."));
	}
}
