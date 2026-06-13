package com.goinggoing.goinggoing.domain.bookmark.controller;

import com.goinggoing.goinggoing.domain.bookmark.dto.BookmarkCreateRequest;
import com.goinggoing.goinggoing.domain.bookmark.dto.BookmarkDeleteRequest;
import com.goinggoing.goinggoing.domain.bookmark.dto.BookmarkResponse;
import com.goinggoing.goinggoing.domain.bookmark.service.BookmarkService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import com.goinggoing.goinggoing.global.security.CurrentUserExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Bookmark", description = "로그인 사용자의 찜 API")
public class BookmarkController {

	private final BookmarkService bookmarkService;
	private final CurrentUserExtractor currentUserExtractor;

	public BookmarkController(BookmarkService bookmarkService, CurrentUserExtractor currentUserExtractor) {
		this.bookmarkService = bookmarkService;
		this.currentUserExtractor = currentUserExtractor;
	}

	@Operation(summary = "찜 목록 조회", description = "로그인 사용자의 찜 목록을 최신순으로 조회합니다.")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "찜 목록 조회 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "401",
					description = "UNAUTHORIZED: 인증 헤더 누락 또는 비활성 사용자",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@GetMapping
	public ResponseEntity<ApiResponse<List<BookmarkResponse>>> getBookmarks(
			@Parameter(hidden = true)
			@RequestHeader("Authorization") String authorizationHeader
	) {
		// 사용자 인증 처리
		Long userId = currentUserExtractor.extractUserId(authorizationHeader);
		// 찜 목록 조회
		List<BookmarkResponse> response = bookmarkService.getBookmarks(userId);
		return ResponseEntity.ok(ApiResponse.success(response, "찜 목록 조회가 완료되었습니다."));
	}

	@Operation(summary = "찜 추가", description = "로그인 사용자가 공개 컨텐츠를 찜합니다.")
	@SecurityRequirement(name = "bearerAuth")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "찜 추가 요청",
			required = true,
			content = @Content(schema = @Schema(implementation = BookmarkCreateRequest.class))
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "찜 추가 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "401",
					description = "UNAUTHORIZED: 인증 헤더 누락 또는 비활성 사용자",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "CONTENT_NOT_FOUND: 존재하지 않거나 비공개/삭제된 컨텐츠",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "409",
					description = "BOOKMARK_ALREADY_EXISTS: 이미 찜한 컨텐츠",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@PostMapping
	public ResponseEntity<ApiResponse<BookmarkResponse>> addBookmark(
			@Parameter(hidden = true)
			@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody BookmarkCreateRequest request
	) {
		// 사용자 인증 처리
		Long userId = currentUserExtractor.extractUserId(authorizationHeader);
		// 찜 추가 처리
		BookmarkResponse response = bookmarkService.addBookmark(userId, request.contentId());
		return ResponseEntity.ok(ApiResponse.success(response, "찜 추가가 완료되었습니다."));
	}

	@Operation(summary = "찜 삭제", description = "로그인 사용자의 특정 찜 항목을 삭제합니다.")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "찜 삭제 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "401",
					description = "UNAUTHORIZED: 인증 헤더 누락 또는 비활성 사용자",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "BOOKMARK_NOT_FOUND: 존재하지 않거나 내 찜이 아닌 항목",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@DeleteMapping("/{bookmarkId}")
	public ResponseEntity<ApiResponse<Void>> deleteBookmark(
			@Parameter(hidden = true)
			@RequestHeader("Authorization") String authorizationHeader,
			@Parameter(description = "찜 ID", example = "1")
			@PathVariable Long bookmarkId
	) {
		// 사용자 인증 처리
		Long userId = currentUserExtractor.extractUserId(authorizationHeader);
		// 찜 삭제 처리
		bookmarkService.deleteBookmark(userId, bookmarkId);
		return ResponseEntity.ok(ApiResponse.success(null, "찜 삭제가 완료되었습니다."));
	}

	@Operation(summary = "찜 일괄 삭제", description = "로그인 사용자의 선택한 찜 항목을 한 번에 삭제합니다.")
	@SecurityRequirement(name = "bearerAuth")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "찜 일괄 삭제 요청",
			required = true,
			content = @Content(schema = @Schema(implementation = BookmarkDeleteRequest.class))
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "찜 일괄 삭제 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "401",
					description = "UNAUTHORIZED: 인증 헤더 누락 또는 비활성 사용자",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@DeleteMapping
	public ResponseEntity<ApiResponse<Void>> deleteBookmarks(
			@Parameter(hidden = true)
			@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody BookmarkDeleteRequest request
	) {
		// 사용자 인증 처리
		Long userId = currentUserExtractor.extractUserId(authorizationHeader);
		// 찜 일괄 삭제 처리
		bookmarkService.deleteBookmarks(userId, request.bookmarkIds());
		return ResponseEntity.ok(ApiResponse.success(null, "찜 일괄 삭제가 완료되었습니다."));
	}
}
