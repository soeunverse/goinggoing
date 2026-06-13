package com.goinggoing.goinggoing.domain.recommendation.controller;

import com.goinggoing.goinggoing.domain.content.dto.ContentSummaryResponse;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.recommendation.dto.RouletteRecommendationCondition;
import com.goinggoing.goinggoing.domain.recommendation.service.RecommendationService;
import com.goinggoing.goinggoing.global.exception.GlobalExceptionHandler;
import com.goinggoing.goinggoing.global.security.CurrentUserExtractor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RecommendationControllerTest {

	private final RecommendationService recommendationService = mock(RecommendationService.class);
	private final CurrentUserExtractor currentUserExtractor = mock(CurrentUserExtractor.class);
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new RecommendationController(recommendationService, currentUserExtractor))
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();

	@Test
	@DisplayName("피드 추천 API는 토큰이 있으면 사용자 ID 기반 추천 결과를 반환한다")
	void getFeedWithAuthorizationSuccess() throws Exception {
		when(currentUserExtractor.extractUserId("Bearer access-token")).thenReturn(1L);
		when(recommendationService.getFeed(1L)).thenReturn(List.of(summary("성심당")));

		mockMvc.perform(get("/api/recommendations/feed")
						.header("Authorization", "Bearer access-token"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].title").value("성심당"))
				.andExpect(jsonPath("$.message").value("피드 추천 조회가 완료되었습니다."))
				.andExpect(jsonPath("$.errorCode").value(nullValue()));
	}

	@Test
	@DisplayName("피드 추천 API는 토큰이 없으면 HOT fallback 결과를 반환한다")
	void getFeedWithoutAuthorizationSuccess() throws Exception {
		when(recommendationService.getFeed(null)).thenReturn(List.of(summary("광안리 해수욕장")));

		mockMvc.perform(get("/api/recommendations/feed"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].title").value("광안리 해수욕장"));
	}

	@Test
	@DisplayName("연관 컨텐츠 추천 API는 200과 추천 목록을 반환한다")
	void getRelatedContentsSuccess() throws Exception {
		when(recommendationService.getRelatedContents(1L)).thenReturn(List.of(summary("대전 중앙시장")));

		mockMvc.perform(get("/api/recommendations/related/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].title").value("대전 중앙시장"))
				.andExpect(jsonPath("$.message").value("연관 컨텐츠 추천 조회가 완료되었습니다."));
	}

	@Test
	@DisplayName("룰렛 후보 추천 API는 조건을 서비스로 전달하고 200을 반환한다")
	void getRouletteCandidatesSuccess() throws Exception {
		RouletteRecommendationCondition condition = new RouletteRecommendationCondition(3L, 1L, 1L, ContentType.RESTAURANT, List.of(1L));
		when(recommendationService.getRouletteCandidates(condition)).thenReturn(List.of(summary("성심당")));

		mockMvc.perform(get("/api/recommendations/roulette")
						.param("regionId", "3")
						.param("themeId", "1")
						.param("subThemeId", "1")
						.param("contentType", "RESTAURANT")
						.param("tagIds", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].title").value("성심당"))
				.andExpect(jsonPath("$.message").value("룰렛 후보 추천 조회가 완료되었습니다."));

		verify(recommendationService).getRouletteCandidates(condition);
	}

	private ContentSummaryResponse summary(String title) {
		return new ContentSummaryResponse(
				1L,
				title,
				"추천 컨텐츠",
				"대전",
				"맛집",
				"https://image.test/thumb.jpg",
				ContentType.RESTAURANT,
				120L,
				30L,
				true
		);
	}
}
