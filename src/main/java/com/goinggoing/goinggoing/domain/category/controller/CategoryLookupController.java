package com.goinggoing.goinggoing.domain.category.controller;

import com.goinggoing.goinggoing.domain.category.dto.CategoryItemResponse;
import com.goinggoing.goinggoing.domain.category.dto.ThemeResponse;
import com.goinggoing.goinggoing.domain.category.service.CategoryLookupService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryLookupController {

	private final CategoryLookupService categoryLookupService;

	public CategoryLookupController(CategoryLookupService categoryLookupService) {
		this.categoryLookupService = categoryLookupService;
	}

	@GetMapping("/regions")
	public ResponseEntity<ApiResponse<List<CategoryItemResponse>>> getRegions() {
		List<CategoryItemResponse> response = categoryLookupService.getRegions();
		return ResponseEntity.ok(ApiResponse.success(response, "지역 목록 조회가 완료되었습니다."));
	}

	@GetMapping("/themes")
	public ResponseEntity<ApiResponse<List<ThemeResponse>>> getThemes() {
		List<ThemeResponse> response = categoryLookupService.getThemes();
		return ResponseEntity.ok(ApiResponse.success(response, "테마 목록 조회가 완료되었습니다."));
	}

	@GetMapping("/themes/{themeId}/sub-themes")
	public ResponseEntity<ApiResponse<List<CategoryItemResponse>>> getSubThemes(@PathVariable Long themeId) {
		List<CategoryItemResponse> response = categoryLookupService.getSubThemes(themeId);
		return ResponseEntity.ok(ApiResponse.success(response, "하위 카테고리 목록 조회가 완료되었습니다."));
	}

	@GetMapping("/tags")
	public ResponseEntity<ApiResponse<List<CategoryItemResponse>>> getTags() {
		List<CategoryItemResponse> response = categoryLookupService.getTags();
		return ResponseEntity.ok(ApiResponse.success(response, "태그 목록 조회가 완료되었습니다."));
	}
}
