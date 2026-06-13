package com.goinggoing.goinggoing.domain.bookmark.service;

import com.goinggoing.goinggoing.domain.bookmark.dto.BookmarkResponse;
import com.goinggoing.goinggoing.domain.bookmark.entity.Bookmark;
import com.goinggoing.goinggoing.domain.bookmark.repository.BookmarkRepository;
import com.goinggoing.goinggoing.domain.category.entity.Region;
import com.goinggoing.goinggoing.domain.category.entity.SubTheme;
import com.goinggoing.goinggoing.domain.category.entity.Theme;
import com.goinggoing.goinggoing.domain.content.entity.Content;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.content.repository.ContentRepository;
import com.goinggoing.goinggoing.domain.user.entity.User;
import com.goinggoing.goinggoing.domain.user.repository.UserRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookmarkServiceTest {

	private UserRepository userRepository;
	private ContentRepository contentRepository;
	private BookmarkRepository bookmarkRepository;
	private BookmarkService bookmarkService;

	@BeforeEach
	void setUp() {
		userRepository = mock(UserRepository.class);
		contentRepository = mock(ContentRepository.class);
		bookmarkRepository = mock(BookmarkRepository.class);
		bookmarkService = new BookmarkService(userRepository, contentRepository, bookmarkRepository);
	}

	@Test
	@DisplayName("로그인 사용자의 찜 목록을 조회한다")
	void getBookmarksSuccess() {
		User user = user();
		Content content = content("성심당", 30L);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(bookmarkRepository.findAllByUserId(1L)).thenReturn(List.of(new Bookmark(1L, user, content, LocalDateTime.now())));

		List<BookmarkResponse> responses = bookmarkService.getBookmarks(1L);

		assertThat(responses).hasSize(1);
		assertThat(responses.get(0).contentTitle()).isEqualTo("성심당");
	}

	@Test
	@DisplayName("컨텐츠를 찜하면 bookmarkCount가 증가하고 찜 응답을 반환한다")
	void addBookmarkSuccess() {
		User user = user();
		Content content = content("성심당", 30L);
		Bookmark bookmark = new Bookmark(1L, user, content, LocalDateTime.now());
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(contentRepository.findPublishedContent(1L)).thenReturn(Optional.of(content));
		when(bookmarkRepository.existsByUserIdAndContentId(1L, 1L)).thenReturn(false);
		when(bookmarkRepository.save(org.mockito.ArgumentMatchers.any(Bookmark.class))).thenReturn(bookmark);

		BookmarkResponse response = bookmarkService.addBookmark(1L, 1L);

		assertThat(response.contentTitle()).isEqualTo("성심당");
		assertThat(content.getBookmarkCount()).isEqualTo(31L);
	}

	@Test
	@DisplayName("이미 찜한 컨텐츠를 다시 찜하면 BOOKMARK_ALREADY_EXISTS 예외가 발생한다")
	void addBookmarkDuplicatedFails() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
		when(contentRepository.findPublishedContent(1L)).thenReturn(Optional.of(content("성심당", 30L)));
		when(bookmarkRepository.existsByUserIdAndContentId(1L, 1L)).thenReturn(true);

		assertThatThrownBy(() -> bookmarkService.addBookmark(1L, 1L))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BOOKMARK_ALREADY_EXISTS));
	}

	@Test
	@DisplayName("찜 단건 삭제는 소유자 검증 후 bookmarkCount를 감소시킨다")
	void deleteBookmarkSuccess() {
		User user = user();
		Content content = content("성심당", 30L);
		Bookmark bookmark = new Bookmark(1L, user, content, LocalDateTime.now());
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(bookmarkRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(bookmark));

		bookmarkService.deleteBookmark(1L, 1L);

		assertThat(content.getBookmarkCount()).isEqualTo(29L);
		verify(bookmarkRepository).delete(bookmark);
	}

	@Test
	@DisplayName("존재하지 않거나 내 찜이 아니면 BOOKMARK_NOT_FOUND 예외가 발생한다")
	void deleteBookmarkNotFoundFails() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
		when(bookmarkRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> bookmarkService.deleteBookmark(1L, 999L))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BOOKMARK_NOT_FOUND));
	}

	@Test
	@DisplayName("찜 일괄 삭제는 선택한 내 찜 목록을 삭제한다")
	void deleteBookmarksSuccess() {
		User user = user();
		Content content = content("성심당", 30L);
		Bookmark bookmark = new Bookmark(1L, user, content, LocalDateTime.now());
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(bookmarkRepository.findAllByIdInAndUserId(List.of(1L), 1L)).thenReturn(List.of(bookmark));

		bookmarkService.deleteBookmarks(1L, List.of(1L));

		assertThat(content.getBookmarkCount()).isEqualTo(29L);
		verify(bookmarkRepository).deleteAll(List.of(bookmark));
	}

	private User user() {
		return User.create("user@example.com", "encoded-password", "즉흥여행자").withId(1L);
	}

	private Content content(String title, Long bookmarkCount) {
		Region region = new Region(3L, "3", "대전", "대전광역시", 3);
		Theme theme = new Theme(1L, "맛집", "먹으러 떠나는 여행", 1);
		SubTheme subTheme = new SubTheme(1L, theme, "빵지순례", "유명 빵집", 1);
		return new Content(
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
				bookmarkCount,
				new BigDecimal("150.00"),
				true
		);
	}
}
