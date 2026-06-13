package com.goinggoing.goinggoing.domain.category.controller;

import com.goinggoing.goinggoing.domain.category.dto.CategoryItemResponse;
import com.goinggoing.goinggoing.domain.category.dto.ThemeResponse;
import com.goinggoing.goinggoing.domain.category.service.CategoryLookupService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category", description = "지역, 테마, 하위 카테고리, 태그 조회 API")
public class CategoryLookupController {

	private final CategoryLookupService categoryLookupService;

	public CategoryLookupController(CategoryLookupService categoryLookupService) {
		this.categoryLookupService = categoryLookupService;
	}

	@Operation(summary = "지역 목록 조회", description = "전국 시/도 단위 지역 목록을 displayOrder 순서로 조회합니다. 인증이 필요 없는 public API입니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "지역 목록 조회 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@GetMapping("/regions")
	public ResponseEntity<ApiResponse<List<CategoryItemResponse>>> getRegions() {
		// 지역 목록 조회
		List<CategoryItemResponse> response = categoryLookupService.getRegions();
		return ResponseEntity.ok(ApiResponse.success(response, "지역 목록 조회가 완료되었습니다."));
	}

	@Operation(summary = "테마 목록 조회", description = "MVP 추천/검색에 사용할 상위 테마 목록을 조회합니다. 인증이 필요 없는 public API입니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "테마 목록 조회 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@GetMapping("/themes")
	public ResponseEntity<ApiResponse<List<ThemeResponse>>> getThemes() {
		// 테마 목록 조회
		List<ThemeResponse> response = categoryLookupService.getThemes();
		return ResponseEntity.ok(ApiResponse.success(response, "테마 목록 조회가 완료되었습니다."));
	}

	@Operation(summary = "하위 카테고리 목록 조회", description = "상위 테마 ID에 속한 하위 카테고리 목록을 조회합니다. 인증이 필요 없는 public API입니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "하위 카테고리 목록 조회 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "CATEGORY_NOT_FOUND: 존재하지 않는 테마 ID",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@GetMapping("/themes/{themeId}/sub-themes")
	public ResponseEntity<ApiResponse<List<CategoryItemResponse>>> getSubThemes(
			@Parameter(description = "상위 테마 ID", example = "1")
			@PathVariable Long themeId
	) {
		// 하위 카테고리 목록 조회
		List<CategoryItemResponse> response = categoryLookupService.getSubThemes(themeId);
		return ResponseEntity.ok(ApiResponse.success(response, "하위 카테고리 목록 조회가 완료되었습니다."));
	}

	@Operation(summary = "태그 목록 조회", description = "컨텐츠 카드와 추천 조건에 사용할 태그 목록을 조회합니다. 인증이 필요 없는 public API입니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "태그 목록 조회 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@GetMapping("/tags")
	public ResponseEntity<ApiResponse<List<CategoryItemResponse>>> getTags() {
		// 태그 목록 조회
		List<CategoryItemResponse> response = categoryLookupService.getTags();
		return ResponseEntity.ok(ApiResponse.success(response, "태그 목록 조회가 완료되었습니다."));
	}
}
