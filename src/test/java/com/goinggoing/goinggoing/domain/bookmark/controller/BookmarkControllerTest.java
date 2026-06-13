package com.goinggoing.goinggoing.domain.bookmark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goinggoing.goinggoing.domain.bookmark.dto.BookmarkCreateRequest;
import com.goinggoing.goinggoing.domain.bookmark.dto.BookmarkDeleteRequest;
import com.goinggoing.goinggoing.domain.bookmark.dto.BookmarkResponse;
import com.goinggoing.goinggoing.domain.bookmark.service.BookmarkService;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import com.goinggoing.goinggoing.global.exception.GlobalExceptionHandler;
import com.goinggoing.goinggoing.global.security.CurrentUserExtractor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookmarkControllerTest {

	private final BookmarkService bookmarkService = mock(BookmarkService.class);
	private final CurrentUserExtractor currentUserExtractor = mock(CurrentUserExtractor.class);
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new BookmarkController(bookmarkService, currentUserExtractor))
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();

	@Test
	@DisplayName("찜 목록 조회 API는 200과 찜 목록을 반환한다")
	void getBookmarksSuccess() throws Exception {
		when(currentUserExtractor.extractUserId("Bearer access-token")).thenReturn(1L);
		when(bookmarkService.getBookmarks(1L)).thenReturn(List.of(bookmark()));

		mockMvc.perform(get("/api/bookmarks")
						.header("Authorization", "Bearer access-token"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].bookmarkId").value(1L))
				.andExpect(jsonPath("$.data[0].contentTitle").value("성심당"))
				.andExpect(jsonPath("$.message").value("찜 목록 조회가 완료되었습니다."))
				.andExpect(jsonPath("$.errorCode").value(nullValue()));
	}

	@Test
	@DisplayName("찜 추가 API는 200과 추가된 찜 정보를 반환한다")
	void addBookmarkSuccess() throws Exception {
		when(currentUserExtractor.extractUserId("Bearer access-token")).thenReturn(1L);
		when(bookmarkService.addBookmark(1L, 1L)).thenReturn(bookmark());

		mockMvc.perform(post("/api/bookmarks")
						.header("Authorization", "Bearer access-token")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(new BookmarkCreateRequest(1L))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.contentId").value(1L))
				.andExpect(jsonPath("$.message").value("찜 추가가 완료되었습니다."));
	}

	@Test
	@DisplayName("이미 찜한 컨텐츠면 409와 BOOKMARK_ALREADY_EXISTS 응답을 반환한다")
	void addBookmarkDuplicatedFails() throws Exception {
		when(currentUserExtractor.extractUserId("Bearer access-token")).thenReturn(1L);
		when(bookmarkService.addBookmark(1L, 1L)).thenThrow(new BusinessException(ErrorCode.BOOKMARK_ALREADY_EXISTS));

		mockMvc.perform(post("/api/bookmarks")
						.header("Authorization", "Bearer access-token")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(new BookmarkCreateRequest(1L))))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("BOOKMARK_ALREADY_EXISTS"));
	}

	@Test
	@DisplayName("찜 단건 삭제 API는 200과 완료 응답을 반환한다")
	void deleteBookmarkSuccess() throws Exception {
		when(currentUserExtractor.extractUserId("Bearer access-token")).thenReturn(1L);

		mockMvc.perform(delete("/api/bookmarks/1")
						.header("Authorization", "Bearer access-token"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("찜 삭제가 완료되었습니다."));

		verify(bookmarkService).deleteBookmark(1L, 1L);
	}

	@Test
	@DisplayName("찜 일괄 삭제 API는 200과 완료 응답을 반환한다")
	void deleteBookmarksSuccess() throws Exception {
		when(currentUserExtractor.extractUserId("Bearer access-token")).thenReturn(1L);

		mockMvc.perform(delete("/api/bookmarks")
						.header("Authorization", "Bearer access-token")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(new BookmarkDeleteRequest(List.of(1L, 2L)))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("찜 일괄 삭제가 완료되었습니다."));

		verify(bookmarkService).deleteBookmarks(1L, List.of(1L, 2L));
	}

	@Test
	@DisplayName("Authorization 헤더가 없으면 401과 UNAUTHORIZED 응답을 반환한다")
	void missingAuthorizationFails() throws Exception {
		mockMvc.perform(get("/api/bookmarks"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
	}

	private BookmarkResponse bookmark() {
		return new BookmarkResponse(
				1L,
				1L,
				"성심당",
				"대전 당일치기 대표 컨텐츠",
				"대전",
				"맛집",
				"https://image.test/thumb.jpg",
				ContentType.RESTAURANT,
				LocalDateTime.of(2026, 6, 14, 10, 0)
		);
	}
}
