package com.goinggoing.goinggoing.domain.content.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goinggoing.goinggoing.domain.content.dto.ContentDetailResponse;
import com.goinggoing.goinggoing.domain.content.dto.ContentManagementCardRequest;
import com.goinggoing.goinggoing.domain.content.dto.ContentManagementRequest;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.content.service.ContentManagementService;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import com.goinggoing.goinggoing.global.exception.GlobalExceptionHandler;
import com.goinggoing.goinggoing.global.security.CurrentUserExtractor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContentManagementControllerTest {

	private final ContentManagementService contentManagementService = mock(ContentManagementService.class);
	private final CurrentUserExtractor currentUserExtractor = mock(CurrentUserExtractor.class);
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new ContentManagementController(contentManagementService, currentUserExtractor))
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();

	@Test
	@DisplayName("컨텐츠 생성 API는 Bearer 토큰 기반 ADMIN 요청을 처리한다")
	void createContentSuccess() throws Exception {
		when(currentUserExtractor.extractUserId("Bearer admin-token")).thenReturn(1L);
		when(contentManagementService.createContent(1L, request("성심당"))).thenReturn(detail("성심당"));

		mockMvc.perform(post("/api/contents")
						.header("Authorization", "Bearer admin-token")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request("성심당"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.title").value("성심당"))
				.andExpect(jsonPath("$.message").value("컨텐츠 생성이 완료되었습니다."));
	}

	@Test
	@DisplayName("ADMIN이 아니면 컨텐츠 생성 API는 403을 반환한다")
	void createContentForbiddenFails() throws Exception {
		when(currentUserExtractor.extractUserId("Bearer user-token")).thenReturn(1L);
		when(contentManagementService.createContent(1L, request("성심당")))
				.thenThrow(new BusinessException(ErrorCode.FORBIDDEN));

		mockMvc.perform(post("/api/contents")
						.header("Authorization", "Bearer user-token")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request("성심당"))))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("FORBIDDEN"));
	}

	@Test
	@DisplayName("컨텐츠 수정 API는 전체 수정 결과를 반환한다")
	void updateContentSuccess() throws Exception {
		when(currentUserExtractor.extractUserId("Bearer admin-token")).thenReturn(1L);
		when(contentManagementService.updateContent(1L, 1L, request("성심당 업데이트"))).thenReturn(detail("성심당 업데이트"));

		mockMvc.perform(put("/api/contents/1")
						.header("Authorization", "Bearer admin-token")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request("성심당 업데이트"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.title").value("성심당 업데이트"))
				.andExpect(jsonPath("$.message").value("컨텐츠 수정이 완료되었습니다."));
	}

	@Test
	@DisplayName("컨텐츠 삭제 API는 soft delete 처리 후 완료 응답을 반환한다")
	void deleteContentSuccess() throws Exception {
		when(currentUserExtractor.extractUserId("Bearer admin-token")).thenReturn(1L);

		mockMvc.perform(delete("/api/contents/1")
						.header("Authorization", "Bearer admin-token"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("컨텐츠 삭제가 완료되었습니다."));

		verify(contentManagementService).deleteContent(1L, 1L);
	}

	private ContentManagementRequest request(String title) {
		return new ContentManagementRequest(
				3L,
				1L,
				1L,
				title,
				ContentType.RESTAURANT,
				"대전 당일치기 대표 컨텐츠",
				"카드뉴스 설명",
				"대전 중구 대종로",
				new BigDecimal("36.32750"),
				new BigDecimal("127.42720"),
				"https://image.test/thumb.jpg",
				true,
				List.of(1L),
				List.of(new ContentManagementCardRequest("대표 메뉴", "튀김소보로와 부추빵", "https://image.test/card.jpg", 1))
		);
	}

	private ContentDetailResponse detail(String title) {
		return new ContentDetailResponse(
				1L,
				title,
				"대전 당일치기 대표 컨텐츠",
				"카드뉴스 설명",
				"대전 중구 대종로",
				new BigDecimal("36.32750"),
				new BigDecimal("127.42720"),
				"https://image.test/thumb.jpg",
				ContentType.RESTAURANT,
				"대전",
				"맛집",
				"빵지순례",
				0L,
				0L,
				false,
				List.of(),
				List.of()
		);
	}
}
