package com.goinggoing.goinggoing.domain.search.controller;

import com.goinggoing.goinggoing.domain.content.dto.ContentSummaryResponse;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.search.dto.PopularSearchKeywordResponse;
import com.goinggoing.goinggoing.domain.search.dto.RecentSearchKeywordResponse;
import com.goinggoing.goinggoing.domain.search.dto.SearchFilterCondition;
import com.goinggoing.goinggoing.domain.search.service.SearchService;
import com.goinggoing.goinggoing.global.exception.GlobalExceptionHandler;
import com.goinggoing.goinggoing.global.security.CurrentUserExtractor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SearchControllerTest {

	private final SearchService searchService = mock(SearchService.class);
	private final CurrentUserExtractor currentUserExtractor = mock(CurrentUserExtractor.class);
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new SearchController(searchService, currentUserExtractor))
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();

	@Test
	@DisplayName("키워드 검색 API는 200과 검색 결과를 반환한다")
	void searchByKeywordSuccess() throws Exception {
		when(searchService.searchByKeyword("성심당", null)).thenReturn(List.of(summary()));

		mockMvc.perform(get("/api/search").param("q", "성심당"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].title").value("성심당"))
				.andExpect(jsonPath("$.message").value("키워드 검색이 완료되었습니다."))
				.andExpect(jsonPath("$.errorCode").value(nullValue()));
	}

	@Test
	@DisplayName("필터 검색 API는 필터 조건을 서비스로 전달하고 200을 반환한다")
	void searchByFilterSuccess() throws Exception {
		when(searchService.searchByFilter(new SearchFilterCondition(3L, 1L, 1L, ContentType.RESTAURANT, List.of(1L))))
				.thenReturn(List.of(summary()));

		mockMvc.perform(get("/api/search/filter")
						.param("regionId", "3")
						.param("themeId", "1")
						.param("subThemeId", "1")
						.param("contentType", "RESTAURANT")
						.param("tagIds", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].title").value("성심당"))
				.andExpect(jsonPath("$.message").value("필터 검색이 완료되었습니다."));

		verify(searchService).searchByFilter(new SearchFilterCondition(3L, 1L, 1L, ContentType.RESTAURANT, List.of(1L)));
	}

	@Test
	@DisplayName("인기 검색어 조회 API는 200과 인기 검색어 목록을 반환한다")
	void getPopularKeywordsSuccess() throws Exception {
		when(searchService.getPopularKeywords()).thenReturn(List.of(new PopularSearchKeywordResponse("성심당", 3L)));

		mockMvc.perform(get("/api/search/popular"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].keyword").value("성심당"))
				.andExpect(jsonPath("$.data[0].searchCount").value(3L))
				.andExpect(jsonPath("$.message").value("인기 검색어 조회가 완료되었습니다."));
	}

	@Test
	@DisplayName("최근 검색어 조회 API는 Bearer 토큰을 검증하고 200을 반환한다")
	void getRecentKeywordsSuccess() throws Exception {
		when(currentUserExtractor.extractUserId("Bearer access-token")).thenReturn(1L);
		when(searchService.getRecentKeywords(1L))
				.thenReturn(List.of(new RecentSearchKeywordResponse("성심당", LocalDateTime.of(2026, 6, 14, 10, 0))));

		mockMvc.perform(get("/api/search/recent")
						.header("Authorization", "Bearer access-token"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].keyword").value("성심당"))
				.andExpect(jsonPath("$.message").value("최근 검색어 조회가 완료되었습니다."));
	}

	@Test
	@DisplayName("최근 검색어 조회에서 Authorization 헤더가 없으면 401을 반환한다")
	void getRecentKeywordsMissingAuthorizationFails() throws Exception {
		mockMvc.perform(get("/api/search/recent"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
	}

	private ContentSummaryResponse summary() {
		return new ContentSummaryResponse(
				1L,
				"성심당",
				"대전 당일치기 대표 컨텐츠",
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
