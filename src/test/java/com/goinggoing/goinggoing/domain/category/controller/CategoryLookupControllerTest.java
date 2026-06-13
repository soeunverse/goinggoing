package com.goinggoing.goinggoing.domain.category.controller;

import com.goinggoing.goinggoing.domain.category.dto.CategoryItemResponse;
import com.goinggoing.goinggoing.domain.category.dto.ThemeResponse;
import com.goinggoing.goinggoing.domain.category.service.CategoryLookupService;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import com.goinggoing.goinggoing.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryLookupControllerTest {

	private final CategoryLookupService categoryLookupService = mock(CategoryLookupService.class);
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new CategoryLookupController(categoryLookupService))
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();

	@Test
	@DisplayName("지역 목록 조회 API는 200과 지역 목록을 반환한다")
	void getRegionsSuccess() throws Exception {
		when(categoryLookupService.getRegions())
				.thenReturn(List.of(new CategoryItemResponse(1L, "대전", 1)));

		mockMvc.perform(get("/api/categories/regions"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].id").value(1L))
				.andExpect(jsonPath("$.data[0].name").value("대전"))
				.andExpect(jsonPath("$.message").value("지역 목록 조회가 완료되었습니다."))
				.andExpect(jsonPath("$.errorCode").value(nullValue()));
	}

	@Test
	@DisplayName("테마 목록 조회 API는 200과 contentCount 포함 테마 목록을 반환한다")
	void getThemesSuccess() throws Exception {
		when(categoryLookupService.getThemes())
				.thenReturn(List.of(new ThemeResponse(1L, "맛집", "먹으러 떠나는 여행", 1, 0L)));

		mockMvc.perform(get("/api/categories/themes"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].name").value("맛집"))
				.andExpect(jsonPath("$.data[0].contentCount").value(0L))
				.andExpect(jsonPath("$.message").value("테마 목록 조회가 완료되었습니다."));
	}

	@Test
	@DisplayName("하위 카테고리 목록 조회 API는 200과 하위 카테고리 목록을 반환한다")
	void getSubThemesSuccess() throws Exception {
		when(categoryLookupService.getSubThemes(1L))
				.thenReturn(List.of(new CategoryItemResponse(10L, "빵지순례", 1)));

		mockMvc.perform(get("/api/categories/themes/1/sub-themes"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].name").value("빵지순례"))
				.andExpect(jsonPath("$.message").value("하위 카테고리 목록 조회가 완료되었습니다."));
	}

	@Test
	@DisplayName("존재하지 않는 테마면 404와 CATEGORY_NOT_FOUND 응답을 반환한다")
	void getSubThemesThemeNotFoundFails() throws Exception {
		when(categoryLookupService.getSubThemes(999L))
				.thenThrow(new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

		mockMvc.perform(get("/api/categories/themes/999/sub-themes"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("CATEGORY_NOT_FOUND"));
	}

	@Test
	@DisplayName("태그 목록 조회 API는 200과 태그 목록을 반환한다")
	void getTagsSuccess() throws Exception {
		when(categoryLookupService.getTags())
				.thenReturn(List.of(new CategoryItemResponse(1L, "성심당", 1)));

		mockMvc.perform(get("/api/categories/tags"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].name").value("성심당"))
				.andExpect(jsonPath("$.message").value("태그 목록 조회가 완료되었습니다."));
	}
}
