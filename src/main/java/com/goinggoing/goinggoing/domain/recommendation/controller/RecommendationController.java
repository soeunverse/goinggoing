package com.goinggoing.goinggoing.domain.recommendation.controller;

import com.goinggoing.goinggoing.domain.content.dto.ContentSummaryResponse;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.recommendation.dto.RouletteRecommendationCondition;
import com.goinggoing.goinggoing.domain.recommendation.service.RecommendationService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import com.goinggoing.goinggoing.global.security.CurrentUserExtractor;
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
public class RecommendationController {

	private final RecommendationService recommendationService;
	private final CurrentUserExtractor currentUserExtractor;

	public RecommendationController(RecommendationService recommendationService, CurrentUserExtractor currentUserExtractor) {
		this.recommendationService = recommendationService;
		this.currentUserExtractor = currentUserExtractor;
	}

	@GetMapping("/feed")
	public ResponseEntity<ApiResponse<List<ContentSummaryResponse>>> getFeed(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader
	) {
		Long userId = authorizationHeader == null ? null : currentUserExtractor.extractUserId(authorizationHeader);
		List<ContentSummaryResponse> response = recommendationService.getFeed(userId);
		return ResponseEntity.ok(ApiResponse.success(response, "피드 추천 조회가 완료되었습니다."));
	}

	@GetMapping("/related/{contentId}")
	public ResponseEntity<ApiResponse<List<ContentSummaryResponse>>> getRelatedContents(@PathVariable Long contentId) {
		List<ContentSummaryResponse> response = recommendationService.getRelatedContents(contentId);
		return ResponseEntity.ok(ApiResponse.success(response, "연관 컨텐츠 추천 조회가 완료되었습니다."));
	}

	@GetMapping("/roulette")
	public ResponseEntity<ApiResponse<List<ContentSummaryResponse>>> getRouletteCandidates(
			@RequestParam(required = false) Long regionId,
			@RequestParam(required = false) Long themeId,
			@RequestParam(required = false) Long subThemeId,
			@RequestParam(required = false) ContentType contentType,
			@RequestParam(required = false) List<Long> tagIds
	) {
		RouletteRecommendationCondition condition = new RouletteRecommendationCondition(
				regionId,
				themeId,
				subThemeId,
				contentType,
				tagIds
		);
		List<ContentSummaryResponse> response = recommendationService.getRouletteCandidates(condition);
		return ResponseEntity.ok(ApiResponse.success(response, "룰렛 후보 추천 조회가 완료되었습니다."));
	}
}
