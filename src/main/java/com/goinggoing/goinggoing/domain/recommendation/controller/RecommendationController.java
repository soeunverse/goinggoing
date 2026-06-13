package com.goinggoing.goinggoing.domain.recommendation.controller;

import com.goinggoing.goinggoing.domain.content.dto.ContentSummaryResponse;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.recommendation.dto.RouletteRecommendationCondition;
import com.goinggoing.goinggoing.domain.recommendation.service.RecommendationService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import com.goinggoing.goinggoing.global.security.CurrentUserExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recommendation", description = "컨텐츠 추천 API")
public class RecommendationController {

	private final RecommendationService recommendationService;
	private final CurrentUserExtractor currentUserExtractor;

	public RecommendationController(RecommendationService recommendationService, CurrentUserExtractor currentUserExtractor) {
		this.recommendationService = recommendationService;
		this.currentUserExtractor = currentUserExtractor;
	}

	@Operation(summary = "피드 추천", description = "Authorization 헤더가 있으면 온보딩 취향 기반으로 추천하고, 없거나 취향이 없으면 HOT 컨텐츠로 fallback합니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "피드 추천 조회 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@GetMapping("/feed")
	public ResponseEntity<ApiResponse<List<ContentSummaryResponse>>> getFeed(
			@Parameter(description = "선택 인증 헤더. 입력하면 개인화 추천, 없으면 HOT fallback", example = "Bearer access-token")
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader
	) {
		// 선택 인증 처리
		Long userId = authorizationHeader == null ? null : currentUserExtractor.extractUserId(authorizationHeader);
		// 피드 추천 조회
		List<ContentSummaryResponse> response = recommendationService.getFeed(userId);
		return ResponseEntity.ok(ApiResponse.success(response, "피드 추천 조회가 완료되었습니다."));
	}

	@Operation(summary = "연관 컨텐츠 추천", description = "현재 컨텐츠와 같은 지역, 테마, 태그를 우선으로 연관 컨텐츠를 추천합니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "연관 컨텐츠 추천 조회 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "CONTENT_NOT_FOUND: 존재하지 않거나 비공개/삭제된 컨텐츠",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@GetMapping("/related/{contentId}")
	public ResponseEntity<ApiResponse<List<ContentSummaryResponse>>> getRelatedContents(
			@Parameter(description = "기준 컨텐츠 ID", example = "1")
			@PathVariable Long contentId
	) {
		// 연관 컨텐츠 추천 조회
		List<ContentSummaryResponse> response = recommendationService.getRelatedContents(contentId);
		return ResponseEntity.ok(ApiResponse.success(response, "연관 컨텐츠 추천 조회가 완료되었습니다."));
	}

	@Operation(summary = "룰렛 후보 추천", description = "지역, 테마, 하위 카테고리, 컨텐츠 유형, 태그 조건에 맞는 랜덤 후보를 최대 8개 추천합니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "룰렛 후보 추천 조회 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@GetMapping("/roulette")
	public ResponseEntity<ApiResponse<List<ContentSummaryResponse>>> getRouletteCandidates(
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
		// 룰렛 조건 생성
		RouletteRecommendationCondition condition = new RouletteRecommendationCondition(
				regionId,
				themeId,
				subThemeId,
				contentType,
				tagIds
		);
		// 룰렛 후보 추천 조회
		List<ContentSummaryResponse> response = recommendationService.getRouletteCandidates(condition);
		return ResponseEntity.ok(ApiResponse.success(response, "룰렛 후보 추천 조회가 완료되었습니다."));
	}
}
