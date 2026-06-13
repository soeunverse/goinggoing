package com.goinggoing.goinggoing.domain.recommendation.service;

import com.goinggoing.goinggoing.domain.category.entity.Region;
import com.goinggoing.goinggoing.domain.category.entity.SubTheme;
import com.goinggoing.goinggoing.domain.category.entity.Tag;
import com.goinggoing.goinggoing.domain.category.entity.Theme;
import com.goinggoing.goinggoing.domain.content.dto.ContentSummaryResponse;
import com.goinggoing.goinggoing.domain.content.entity.Content;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.content.repository.ContentRepository;
import com.goinggoing.goinggoing.domain.recommendation.dto.RouletteRecommendationCondition;
import com.goinggoing.goinggoing.domain.user.entity.TripDurationType;
import com.goinggoing.goinggoing.domain.user.entity.User;
import com.goinggoing.goinggoing.domain.user.entity.UserPreference;
import com.goinggoing.goinggoing.domain.user.repository.UserPreferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecommendationServiceTest {

	private ContentRepository contentRepository;
	private UserPreferenceRepository userPreferenceRepository;
	private RecommendationService recommendationService;

	@BeforeEach
	void setUp() {
		contentRepository = mock(ContentRepository.class);
		userPreferenceRepository = mock(UserPreferenceRepository.class);
		recommendationService = new RecommendationService(contentRepository, userPreferenceRepository);
	}

	@Test
	@DisplayName("로그인 사용자의 취향이 있으면 취향 기반 피드를 반환한다")
	void getFeedWithPreferenceSuccess() {
		User user = User.create("user@example.com", "encoded-password", "즉흥여행자").withId(1L);
		UserPreference preference = UserPreference.create(
				user,
				TripDurationType.DAY_TRIP,
				List.of(3L),
				List.of(1L),
				List.of(1L)
		);
		when(userPreferenceRepository.findByUserId(1L)).thenReturn(Optional.of(preference));
		when(contentRepository.findRecommendedFeed(List.of(3L), List.of(1L), List.of(1L)))
				.thenReturn(List.of(content("성심당", 3L, 1L)));

		List<ContentSummaryResponse> responses = recommendationService.getFeed(1L);

		assertThat(responses).extracting(ContentSummaryResponse::title).containsExactly("성심당");
	}

	@Test
	@DisplayName("비로그인 사용자는 HOT 컨텐츠 피드를 반환한다")
	void getFeedFallbackSuccess() {
		when(contentRepository.findHotContents()).thenReturn(List.of(content("광안리 해수욕장", 6L, 2L)));

		List<ContentSummaryResponse> responses = recommendationService.getFeed(null);

		assertThat(responses).extracting(ContentSummaryResponse::title).containsExactly("광안리 해수욕장");
	}

	@Test
	@DisplayName("연관 컨텐츠는 같은 지역, 테마, 태그 기준으로 반환한다")
	void getRelatedContentsSuccess() {
		Content baseContent = content("성심당", 3L, 1L);
		when(contentRepository.findPublishedContent(1L)).thenReturn(Optional.of(baseContent));
		when(contentRepository.findRelatedContents(1L, 3L, 1L, List.of(1L)))
				.thenReturn(List.of(content("대전 중앙시장", 3L, 4L)));

		List<ContentSummaryResponse> responses = recommendationService.getRelatedContents(1L);

		assertThat(responses).extracting(ContentSummaryResponse::title).containsExactly("대전 중앙시장");
	}

	@Test
	@DisplayName("룰렛 추천은 조건 기반 최대 8개 후보를 반환한다")
	void getRouletteCandidatesSuccess() {
		RouletteRecommendationCondition condition = new RouletteRecommendationCondition(3L, 1L, 1L, ContentType.RESTAURANT, List.of(1L));
		when(contentRepository.findRouletteCandidates(3L, 1L, 1L, ContentType.RESTAURANT, List.of(1L)))
				.thenReturn(List.of(content("성심당", 3L, 1L)));

		List<ContentSummaryResponse> responses = recommendationService.getRouletteCandidates(condition);

		assertThat(responses).extracting(ContentSummaryResponse::title).containsExactly("성심당");
	}

	private Content content(String title, Long regionId, Long themeId) {
		Region region = new Region(regionId, "3", regionId == 3L ? "대전" : "부산", "지역", 1);
		Theme theme = new Theme(themeId, themeId == 1L ? "맛집" : "자연/바다", "테마", 1);
		SubTheme subTheme = new SubTheme(1L, theme, "빵지순례", "유명 빵집", 1);
		Content content = new Content(
				1L,
				region,
				theme,
				subTheme,
				title,
				ContentType.RESTAURANT,
				"추천용 컨텐츠",
				"카드뉴스 설명",
				"대전 중구 대종로",
				new BigDecimal("36.32750"),
				new BigDecimal("127.42720"),
				"https://image.test/thumb.jpg",
				120L,
				30L,
				new BigDecimal("150.00"),
				true
		);
		content.addTag(new Tag(1L, "빵지순례", 1));
		return content;
	}
}
