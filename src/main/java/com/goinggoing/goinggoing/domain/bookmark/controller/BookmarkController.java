package com.goinggoing.goinggoing.domain.bookmark.controller;

import com.goinggoing.goinggoing.domain.bookmark.dto.BookmarkCreateRequest;
import com.goinggoing.goinggoing.domain.bookmark.dto.BookmarkDeleteRequest;
import com.goinggoing.goinggoing.domain.bookmark.dto.BookmarkResponse;
import com.goinggoing.goinggoing.domain.bookmark.service.BookmarkService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import com.goinggoing.goinggoing.global.security.CurrentUserExtractor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

	private final BookmarkService bookmarkService;
	private final CurrentUserExtractor currentUserExtractor;

	public BookmarkController(BookmarkService bookmarkService, CurrentUserExtractor currentUserExtractor) {
		this.bookmarkService = bookmarkService;
		this.currentUserExtractor = currentUserExtractor;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<BookmarkResponse>>> getBookmarks(
			@RequestHeader("Authorization") String authorizationHeader
	) {
		Long userId = currentUserExtractor.extractUserId(authorizationHeader);
		List<BookmarkResponse> response = bookmarkService.getBookmarks(userId);
		return ResponseEntity.ok(ApiResponse.success(response, "찜 목록 조회가 완료되었습니다."));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<BookmarkResponse>> addBookmark(
			@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody BookmarkCreateRequest request
	) {
		Long userId = currentUserExtractor.extractUserId(authorizationHeader);
		BookmarkResponse response = bookmarkService.addBookmark(userId, request.contentId());
		return ResponseEntity.ok(ApiResponse.success(response, "찜 추가가 완료되었습니다."));
	}

	@DeleteMapping("/{bookmarkId}")
	public ResponseEntity<ApiResponse<Void>> deleteBookmark(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Long bookmarkId
	) {
		Long userId = currentUserExtractor.extractUserId(authorizationHeader);
		bookmarkService.deleteBookmark(userId, bookmarkId);
		return ResponseEntity.ok(ApiResponse.success(null, "찜 삭제가 완료되었습니다."));
	}

	@DeleteMapping
	public ResponseEntity<ApiResponse<Void>> deleteBookmarks(
			@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody BookmarkDeleteRequest request
	) {
		Long userId = currentUserExtractor.extractUserId(authorizationHeader);
		bookmarkService.deleteBookmarks(userId, request.bookmarkIds());
		return ResponseEntity.ok(ApiResponse.success(null, "찜 일괄 삭제가 완료되었습니다."));
	}
}
