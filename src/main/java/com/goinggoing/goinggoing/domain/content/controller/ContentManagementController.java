package com.goinggoing.goinggoing.domain.content.controller;

import com.goinggoing.goinggoing.domain.content.dto.ContentDetailResponse;
import com.goinggoing.goinggoing.domain.content.dto.ContentManagementRequest;
import com.goinggoing.goinggoing.domain.content.service.ContentManagementService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import com.goinggoing.goinggoing.global.security.CurrentUserExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "컨텐츠 관리", description = "관리자 전용 컨텐츠 생성, 수정, 삭제 API")
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

	@Operation(summary = "컨텐츠 생성", description = "관리자가 컨텐츠, 카드뉴스, 태그 연결을 생성합니다.")
	@SecurityRequirement(name = "bearerAuth")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "컨텐츠 생성 요청",
			required = true,
			content = @Content(schema = @Schema(implementation = ContentManagementRequest.class))
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "컨텐츠 생성 성공", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "UNAUTHORIZED: 인증 헤더 누락 또는 비활성 사용자", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "FORBIDDEN: 관리자 권한 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "CATEGORY_NOT_FOUND: 지역/테마/태그 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
	})
	@PostMapping
	public ResponseEntity<ApiResponse<ContentDetailResponse>> createContent(
			@Parameter(hidden = true)
			@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody ContentManagementRequest request
	) {
		// 관리자 인증 처리
		Long adminUserId = currentUserExtractor.extractUserId(authorizationHeader);
		// 컨텐츠 생성 처리
		ContentDetailResponse response = contentManagementService.createContent(adminUserId, request);
		return ResponseEntity.ok(ApiResponse.success(response, "컨텐츠 생성이 완료되었습니다."));
	}

	@Operation(summary = "컨텐츠 수정", description = "관리자가 컨텐츠 정보를 수정하고 카드뉴스/태그를 전체 교체합니다.")
	@SecurityRequirement(name = "bearerAuth")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "컨텐츠 수정 요청",
			required = true,
			content = @Content(schema = @Schema(implementation = ContentManagementRequest.class))
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "컨텐츠 수정 성공", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "UNAUTHORIZED: 인증 헤더 누락 또는 비활성 사용자", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "FORBIDDEN: 관리자 권한 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "CONTENT_NOT_FOUND 또는 CATEGORY_NOT_FOUND", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
	})
	@PutMapping("/{contentId}")
	public ResponseEntity<ApiResponse<ContentDetailResponse>> updateContent(
			@Parameter(hidden = true)
			@RequestHeader("Authorization") String authorizationHeader,
			@Parameter(description = "컨텐츠 ID", example = "1")
			@PathVariable Long contentId,
			@RequestBody ContentManagementRequest request
	) {
		// 관리자 인증 처리
		Long adminUserId = currentUserExtractor.extractUserId(authorizationHeader);
		// 컨텐츠 수정 처리
		ContentDetailResponse response = contentManagementService.updateContent(adminUserId, contentId, request);
		return ResponseEntity.ok(ApiResponse.success(response, "컨텐츠 수정이 완료되었습니다."));
	}

	@Operation(summary = "컨텐츠 삭제", description = "관리자가 컨텐츠를 삭제 처리합니다. 공개 여부를 false로 바꾸고 삭제 시각을 저장합니다.")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "컨텐츠 삭제 성공", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "UNAUTHORIZED: 인증 헤더 누락 또는 비활성 사용자", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "FORBIDDEN: 관리자 권한 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "CONTENT_NOT_FOUND: 컨텐츠 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
	})
	@DeleteMapping("/{contentId}")
	public ResponseEntity<ApiResponse<Void>> deleteContent(
			@Parameter(hidden = true)
			@RequestHeader("Authorization") String authorizationHeader,
			@Parameter(description = "컨텐츠 ID", example = "1")
			@PathVariable Long contentId
	) {
		// 관리자 인증 처리
		Long adminUserId = currentUserExtractor.extractUserId(authorizationHeader);
		// 컨텐츠 삭제 처리
		contentManagementService.deleteContent(adminUserId, contentId);
		return ResponseEntity.ok(ApiResponse.success(null, "컨텐츠 삭제가 완료되었습니다."));
	}
}
