package com.goinggoing.goinggoing.domain.search.service;

import com.goinggoing.goinggoing.domain.category.entity.Region;
import com.goinggoing.goinggoing.domain.category.entity.SubTheme;
import com.goinggoing.goinggoing.domain.category.entity.Tag;
import com.goinggoing.goinggoing.domain.category.entity.Theme;
import com.goinggoing.goinggoing.domain.content.dto.ContentSummaryResponse;
import com.goinggoing.goinggoing.domain.content.entity.Content;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.content.repository.ContentRepository;
import com.goinggoing.goinggoing.domain.search.dto.PopularSearchKeywordResponse;
import com.goinggoing.goinggoing.domain.search.dto.RecentSearchKeywordResponse;
import com.goinggoing.goinggoing.domain.search.dto.SearchFilterCondition;
import com.goinggoing.goinggoing.domain.search.entity.SearchLog;
import com.goinggoing.goinggoing.domain.search.repository.SearchLogRepository;
import com.goinggoing.goinggoing.domain.user.entity.User;
import com.goinggoing.goinggoing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SearchServiceTest {

	private ContentRepository contentRepository;
	private SearchLogRepository searchLogRepository;
	private UserRepository userRepository;
	private SearchService searchService;

	@BeforeEach
	void setUp() {
		contentRepository = mock(ContentRepository.class);
		searchLogRepository = mock(SearchLogRepository.class);
		userRepository = mock(UserRepository.class);
		searchService = new SearchService(contentRepository, searchLogRepository, userRepository);
	}

	@Test
	@DisplayName("키워드 검색은 제목, 요약, 주소, 태그 기준 결과를 반환하고 검색 로그를 저장한다")
	void searchByKeywordSuccess() {
		Content content = content("성심당");
		when(contentRepository.searchByKeyword("성심당")).thenReturn(List.of(content));

		List<ContentSummaryResponse> responses = searchService.searchByKeyword(" 성심당 ", null);

		assertThat(responses).extracting(ContentSummaryResponse::title).containsExactly("성심당");
		verify(searchLogRepository).save(any(SearchLog.class));
	}

	@Test
	@DisplayName("필터 검색은 지역, 테마, 하위 카테고리, 컨텐츠 유형, 태그 조건으로 결과를 반환한다")
	void searchByFilterSuccess() {
		SearchFilterCondition condition = new SearchFilterCondition(3L, 1L, 1L, ContentType.RESTAURANT, List.of(1L));
		when(contentRepository.searchByFilter(3L, 1L, 1L, ContentType.RESTAURANT, List.of(1L)))
				.thenReturn(List.of(content("성심당")));

		List<ContentSummaryResponse> responses = searchService.searchByFilter(condition);

		assertThat(responses).extracting(ContentSummaryResponse::title).containsExactly("성심당");
	}

	@Test
	@DisplayName("인기 검색어는 검색 로그 누적 수 기준 상위 10개를 반환한다")
	void getPopularKeywordsSuccess() {
		when(searchLogRepository.findPopularKeywords(10)).thenReturn(List.of(new PopularSearchKeywordResponse("성심당", 3L)));

		List<PopularSearchKeywordResponse> responses = searchService.getPopularKeywords();

		assertThat(responses).extracting(PopularSearchKeywordResponse::keyword).containsExactly("성심당");
		assertThat(responses.get(0).searchCount()).isEqualTo(3L);
	}

	@Test
	@DisplayName("최근 검색어는 로그인 사용자의 최근 10개를 반환한다")
	void getRecentKeywordsSuccess() {
		User user = User.create("user@example.com", "encoded-password", "즉흥여행자").withId(1L);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(searchLogRepository.findRecentKeywordsByUserId(1L, 10))
				.thenReturn(List.of(new RecentSearchKeywordResponse("성심당", LocalDateTime.of(2026, 6, 14, 10, 0))));

		List<RecentSearchKeywordResponse> responses = searchService.getRecentKeywords(1L);

		assertThat(responses).extracting(RecentSearchKeywordResponse::keyword).containsExactly("성심당");
	}

	private Content content(String title) {
		Region region = new Region(3L, "3", "대전", "대전광역시", 3);
		Theme theme = new Theme(1L, "맛집", "먹으러 떠나는 여행", 1);
		SubTheme subTheme = new SubTheme(1L, theme, "빵지순례", "유명 빵집", 1);
		Content content = new Content(
				1L,
				region,
				theme,
				subTheme,
				title,
				ContentType.RESTAURANT,
				"대전 당일치기 대표 컨텐츠",
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
