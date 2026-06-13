package com.goinggoing.goinggoing.domain.content.controller;

import com.goinggoing.goinggoing.domain.content.dto.ContentCardResponse;
import com.goinggoing.goinggoing.domain.content.dto.ContentDetailResponse;
import com.goinggoing.goinggoing.domain.content.dto.ContentSummaryResponse;
import com.goinggoing.goinggoing.domain.content.dto.ContentTagResponse;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.content.service.ContentLookupService;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import com.goinggoing.goinggoing.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContentLookupControllerTest {

	private final ContentLookupService contentLookupService = mock(ContentLookupService.class);
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new ContentLookupController(contentLookupService))
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();

	@Test
	@DisplayName("컨텐츠 목록 조회 API는 200과 카드 목록을 반환한다")
	void getContentsSuccess() throws Exception {
		when(contentLookupService.getContents(3L, 1L, ContentType.RESTAURANT))
				.thenReturn(List.of(summary()));

		mockMvc.perform(get("/api/contents")
						.param("regionId", "3")
						.param("themeId", "1")
						.param("contentType", "RESTAURANT"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].title").value("성심당"))
				.andExpect(jsonPath("$.data[0].hot").value(true))
				.andExpect(jsonPath("$.message").value("컨텐츠 목록 조회가 완료되었습니다."))
				.andExpect(jsonPath("$.errorCode").value(nullValue()));

		verify(contentLookupService).getContents(3L, 1L, ContentType.RESTAURANT);
	}

	@Test
	@DisplayName("컨텐츠 상세 조회 API는 200과 카드뉴스, 태그를 반환한다")
	void getContentDetailSuccess() throws Exception {
		when(contentLookupService.getContentDetail(1L)).thenReturn(detail());

		mockMvc.perform(get("/api/contents/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.title").value("성심당"))
				.andExpect(jsonPath("$.data.cards[0].title").value("대표 메뉴"))
				.andExpect(jsonPath("$.data.tags[0].name").value("빵지순례"))
				.andExpect(jsonPath("$.message").value("컨텐츠 상세 조회가 완료되었습니다."));
	}

	@Test
	@DisplayName("존재하지 않는 컨텐츠 상세 조회는 404와 CONTENT_NOT_FOUND 응답을 반환한다")
	void getContentDetailNotFoundFails() throws Exception {
		when(contentLookupService.getContentDetail(999L))
				.thenThrow(new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

		mockMvc.perform(get("/api/contents/999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("CONTENT_NOT_FOUND"));
	}

	@Test
	@DisplayName("HOT 컨텐츠 조회 API는 200과 HOT 컨텐츠 목록을 반환한다")
	void getHotContentsSuccess() throws Exception {
		when(contentLookupService.getHotContents()).thenReturn(List.of(summary()));

		mockMvc.perform(get("/api/contents/hot"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].title").value("성심당"))
				.andExpect(jsonPath("$.data[0].hot").value(true))
				.andExpect(jsonPath("$.message").value("HOT 컨텐츠 조회가 완료되었습니다."));
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

	private ContentDetailResponse detail() {
		return new ContentDetailResponse(
				1L,
				"성심당",
				"대전 당일치기 대표 컨텐츠",
				"카드뉴스로 보기 좋은 상세 설명",
				"대전 중구 대종로",
				new BigDecimal("36.32750"),
				new BigDecimal("127.42720"),
				"https://image.test/thumb.jpg",
				ContentType.RESTAURANT,
				"대전",
				"맛집",
				"빵지순례",
				121L,
				30L,
				true,
				List.of(new ContentCardResponse(1L, "대표 메뉴", "튀김소보로와 부추빵", "https://image.test/card.jpg", 1)),
				List.of(new ContentTagResponse(1L, "빵지순례"))
		);
	}
}
