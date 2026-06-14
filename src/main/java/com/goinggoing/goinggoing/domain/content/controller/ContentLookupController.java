package com.goinggoing.goinggoing.domain.content.controller;

import com.goinggoing.goinggoing.domain.content.dto.ContentDetailResponse;
import com.goinggoing.goinggoing.domain.content.dto.ContentSummaryResponse;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.content.service.ContentLookupService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contents")
@Tag(name = "컨텐츠 조회", description = "대표 장소 컨텐츠 목록, 상세, HOT 컨텐츠 조회 API")
public class ContentLookupController {

	private final ContentLookupService contentLookupService;

	public ContentLookupController(ContentLookupService contentLookupService) {
		this.contentLookupService = contentLookupService;
	}

	@Operation(summary = "컨텐츠 목록 조회", description = "공개된 대표 장소 컨텐츠를 HOT 점수 순서로 조회합니다. 지역, 테마, 컨텐츠 유형 필터를 선택적으로 사용할 수 있습니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "컨텐츠 목록 조회 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@GetMapping
	public ResponseEntity<ApiResponse<List<ContentSummaryResponse>>> getContents(
			@Parameter(description = "지역 ID", example = "3")
			@RequestParam(required = false) Long regionId,
			@Parameter(description = "테마 ID", example = "1")
			@RequestParam(required = false) Long themeId,
			@Parameter(description = "컨텐츠 유형", example = "RESTAURANT")
			@RequestParam(required = false) ContentType contentType
	) {
		// 컨텐츠 목록 조회
		List<ContentSummaryResponse> response = contentLookupService.getContents(regionId, themeId, contentType);
		return ResponseEntity.ok(ApiResponse.success(response, "컨텐츠 목록 조회가 완료되었습니다."));
	}

	@Operation(summary = "HOT 컨텐츠 조회", description = "조회수, 찜수, HOT 점수를 기준으로 상위 컨텐츠를 조회합니다. 인증이 필요 없는 공개 API입니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "HOT 컨텐츠 조회 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@GetMapping("/hot")
	public ResponseEntity<ApiResponse<List<ContentSummaryResponse>>> getHotContents() {
		// HOT 컨텐츠 조회
		List<ContentSummaryResponse> response = contentLookupService.getHotContents();
		return ResponseEntity.ok(ApiResponse.success(response, "HOT 컨텐츠 조회가 완료되었습니다."));
	}

	@Operation(summary = "컨텐츠 상세 조회", description = "대표 장소 상세 정보, 카드뉴스, 태그를 조회하고 조회수를 1 증가시킵니다. 인증이 필요 없는 공개 API입니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "컨텐츠 상세 조회 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "CONTENT_NOT_FOUND: 존재하지 않거나 비공개/삭제된 컨텐츠",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@GetMapping("/{contentId}")
	public ResponseEntity<ApiResponse<ContentDetailResponse>> getContentDetail(
			@Parameter(description = "컨텐츠 ID", example = "1")
			@PathVariable Long contentId
	) {
		// 컨텐츠 상세 조회
		ContentDetailResponse response = contentLookupService.getContentDetail(contentId);
		return ResponseEntity.ok(ApiResponse.success(response, "컨텐츠 상세 조회가 완료되었습니다."));
	}
}
