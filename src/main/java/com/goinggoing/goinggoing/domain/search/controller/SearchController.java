package com.goinggoing.goinggoing.domain.search.controller;

import com.goinggoing.goinggoing.domain.content.dto.ContentSummaryResponse;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.search.dto.PopularSearchKeywordResponse;
import com.goinggoing.goinggoing.domain.search.dto.RecentSearchKeywordResponse;
import com.goinggoing.goinggoing.domain.search.dto.SearchFilterCondition;
import com.goinggoing.goinggoing.domain.search.service.SearchService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import com.goinggoing.goinggoing.global.security.CurrentUserExtractor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

	private final SearchService searchService;
	private final CurrentUserExtractor currentUserExtractor;

	public SearchController(SearchService searchService, CurrentUserExtractor currentUserExtractor) {
		this.searchService = searchService;
		this.currentUserExtractor = currentUserExtractor;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<ContentSummaryResponse>>> searchByKeyword(@RequestParam("q") String keyword) {
		List<ContentSummaryResponse> response = searchService.searchByKeyword(keyword, null);
		return ResponseEntity.ok(ApiResponse.success(response, "키워드 검색이 완료되었습니다."));
	}

	@GetMapping("/filter")
	public ResponseEntity<ApiResponse<List<ContentSummaryResponse>>> searchByFilter(
			@RequestParam(required = false) Long regionId,
			@RequestParam(required = false) Long themeId,
			@RequestParam(required = false) Long subThemeId,
			@RequestParam(required = false) ContentType contentType,
			@RequestParam(required = false) List<Long> tagIds
	) {
		SearchFilterCondition condition = new SearchFilterCondition(regionId, themeId, subThemeId, contentType, tagIds);
		List<ContentSummaryResponse> response = searchService.searchByFilter(condition);
		return ResponseEntity.ok(ApiResponse.success(response, "필터 검색이 완료되었습니다."));
	}

	@GetMapping("/popular")
	public ResponseEntity<ApiResponse<List<PopularSearchKeywordResponse>>> getPopularKeywords() {
		List<PopularSearchKeywordResponse> response = searchService.getPopularKeywords();
		return ResponseEntity.ok(ApiResponse.success(response, "인기 검색어 조회가 완료되었습니다."));
	}

	@GetMapping("/recent")
	public ResponseEntity<ApiResponse<List<RecentSearchKeywordResponse>>> getRecentKeywords(
			@RequestHeader("Authorization") String authorizationHeader
	) {
		Long userId = currentUserExtractor.extractUserId(authorizationHeader);
		List<RecentSearchKeywordResponse> response = searchService.getRecentKeywords(userId);
		return ResponseEntity.ok(ApiResponse.success(response, "최근 검색어 조회가 완료되었습니다."));
	}
}
