package com.goinggoing.goinggoing.domain.search.controller;

import com.goinggoing.goinggoing.domain.content.dto.ContentSummaryResponse;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.search.dto.PopularSearchKeywordResponse;
import com.goinggoing.goinggoing.domain.search.dto.RecentSearchKeywordResponse;
import com.goinggoing.goinggoing.domain.search.dto.SearchFilterCondition;
import com.goinggoing.goinggoing.domain.search.service.SearchService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@Tag(name = "Search", description = "컨텐츠 검색 API")
public class SearchController {

	private final SearchService searchService;
	private final CurrentUserExtractor currentUserExtractor;

	public SearchController(SearchService searchService, CurrentUserExtractor currentUserExtractor) {
		this.searchService = searchService;
		this.currentUserExtractor = currentUserExtractor;
	}

	@Operation(summary = "키워드 검색", description = "제목, 요약, 주소, 태그 기준으로 공개 컨텐츠를 검색합니다. 인증이 필요 없는 public API입니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "키워드 검색 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "BAD_REQUEST: 빈 검색어",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@GetMapping
	public ResponseEntity<ApiResponse<List<ContentSummaryResponse>>> searchByKeyword(
			@Parameter(description = "검색어", example = "성심당")
			@RequestParam("q") String keyword
	) {
		// 키워드 검색 처리
		List<ContentSummaryResponse> response = searchService.searchByKeyword(keyword, null);
		return ResponseEntity.ok(ApiResponse.success(response, "키워드 검색이 완료되었습니다."));
	}

	@Operation(summary = "필터 검색", description = "지역, 테마, 하위 카테고리, 컨텐츠 유형, 태그 조건을 AND 조건으로 검색합니다. 인증이 필요 없는 public API입니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "필터 검색 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@GetMapping("/filter")
	public ResponseEntity<ApiResponse<List<ContentSummaryResponse>>> searchByFilter(
			@Parameter(description = "지역 ID", example = "3")
			@RequestParam(required = false) Long regionId,
			@Parameter(description = "테마 ID", example = "1")
			@RequestParam(required = false) Long themeId,
			@Parameter(description = "하위 카테고리 ID", example = "1")
			@RequestParam(required = false) Long subThemeId,
			@Parameter(description = "컨텐츠 유형", example = "RESTAURANT")
			@RequestParam(required = false) ContentType contentType,
			@Parameter(description = "태그 ID 목록", example = "1")
			@RequestParam(required = false) List<Long> tagIds
	) {
		// 필터 조건 생성
		SearchFilterCondition condition = new SearchFilterCondition(regionId, themeId, subThemeId, contentType, tagIds);
		// 필터 검색 처리
		List<ContentSummaryResponse> response = searchService.searchByFilter(condition);
		return ResponseEntity.ok(ApiResponse.success(response, "필터 검색이 완료되었습니다."));
	}

	@Operation(summary = "인기 검색어 조회", description = "전체 검색 로그 누적 횟수 기준 상위 검색어를 조회합니다. 인증이 필요 없는 public API입니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "인기 검색어 조회 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@GetMapping("/popular")
	public ResponseEntity<ApiResponse<List<PopularSearchKeywordResponse>>> getPopularKeywords() {
		// 인기 검색어 조회
		List<PopularSearchKeywordResponse> response = searchService.getPopularKeywords();
		return ResponseEntity.ok(ApiResponse.success(response, "인기 검색어 조회가 완료되었습니다."));
	}

	@Operation(summary = "최근 검색어 조회", description = "로그인 사용자의 최근 검색어를 최신순으로 조회합니다.")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "최근 검색어 조회 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "401",
					description = "UNAUTHORIZED: 인증 헤더 누락 또는 비활성 사용자",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@GetMapping("/recent")
	public ResponseEntity<ApiResponse<List<RecentSearchKeywordResponse>>> getRecentKeywords(
			@Parameter(hidden = true)
			@RequestHeader("Authorization") String authorizationHeader
	) {
		// 사용자 인증 처리
		Long userId = currentUserExtractor.extractUserId(authorizationHeader);
		// 최근 검색어 조회
		List<RecentSearchKeywordResponse> response = searchService.getRecentKeywords(userId);
		return ResponseEntity.ok(ApiResponse.success(response, "최근 검색어 조회가 완료되었습니다."));
	}
}
