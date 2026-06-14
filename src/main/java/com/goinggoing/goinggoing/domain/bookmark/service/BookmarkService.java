package com.goinggoing.goinggoing.domain.bookmark.service;

import com.goinggoing.goinggoing.domain.bookmark.dto.BookmarkResponse;
import com.goinggoing.goinggoing.domain.bookmark.entity.Bookmark;
import com.goinggoing.goinggoing.domain.bookmark.repository.BookmarkRepository;
import com.goinggoing.goinggoing.domain.content.entity.Content;
import com.goinggoing.goinggoing.domain.content.repository.ContentRepository;
import com.goinggoing.goinggoing.domain.user.entity.User;
import com.goinggoing.goinggoing.domain.user.repository.UserRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class BookmarkService {

	private final UserRepository userRepository;
	private final ContentRepository contentRepository;
	private final BookmarkRepository bookmarkRepository;

	public BookmarkService(
			UserRepository userRepository,
			ContentRepository contentRepository,
			BookmarkRepository bookmarkRepository
	) {
		this.userRepository = userRepository;
		this.contentRepository = contentRepository;
		this.bookmarkRepository = bookmarkRepository;
	}

	@Transactional(readOnly = true)
	public List<BookmarkResponse> getBookmarks(Long userId) {
		// 사용자 상태 검증
		validateActiveUser(userId);

		// 찜 목록 응답 생성
		return bookmarkRepository.findAllByUserId(userId)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional
	public BookmarkResponse addBookmark(Long userId, Long contentId) {
		// 사용자 상태 검증
		User user = validateActiveUser(userId);
		// 공개 컨텐츠 조회
		Content content = contentRepository.findPublishedContent(contentId)
				.orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

		// 중복 찜 검증
		if (bookmarkRepository.existsByUserIdAndContentId(userId, contentId)) {
			throw new BusinessException(ErrorCode.BOOKMARK_ALREADY_EXISTS);
		}

		// 찜수 증가
		content.increaseBookmarkCount();
		Bookmark bookmark = bookmarkRepository.save(Bookmark.create(user, content));
		log.info("[DB 저장] 찜 추가 userId={} contentId={} bookmarkId={} bookmarkCount={}", userId, contentId, bookmark.getId(), content.getBookmarkCount());

		return toResponse(bookmark);
	}

	@Transactional
	public void deleteBookmark(Long userId, Long bookmarkId) {
		// 사용자 상태 검증
		validateActiveUser(userId);
		// 내 찜 항목 조회
		Bookmark bookmark = bookmarkRepository.findByIdAndUserId(bookmarkId, userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.BOOKMARK_NOT_FOUND));

		// 찜수 감소
		bookmark.getContent().decreaseBookmarkCount();
		bookmarkRepository.delete(bookmark);
		log.info("[DB 삭제] 찜 삭제 userId={} contentId={} bookmarkId={} bookmarkCount={}", userId, bookmark.getContent().getId(), bookmarkId, bookmark.getContent().getBookmarkCount());
	}

	@Transactional
	public void deleteBookmarks(Long userId, List<Long> bookmarkIds) {
		// 사용자 상태 검증
		validateActiveUser(userId);
		// 내 찜 항목 목록 조회
		List<Bookmark> bookmarks = bookmarkRepository.findAllByIdInAndUserId(bookmarkIds, userId);

		// 찜수 일괄 감소
		bookmarks.forEach(bookmark -> bookmark.getContent().decreaseBookmarkCount());
		bookmarkRepository.deleteAll(bookmarks);
		log.info("[DB 삭제] 찜 일괄 삭제 userId={} requestedCount={} deletedCount={}", userId, bookmarkIds.size(), bookmarks.size());
	}

	private User validateActiveUser(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.UNAUTHORIZED);
		}
		return user;
	}

	private BookmarkResponse toResponse(Bookmark bookmark) {
		Content content = bookmark.getContent();
		return new BookmarkResponse(
				bookmark.getId(),
				content.getId(),
				content.getTitle(),
				content.getSummary(),
				content.getRegion().getName(),
				content.getTheme() == null ? null : content.getTheme().getName(),
				content.getThumbnailUrl(),
				content.getContentType(),
				bookmark.getCreatedAt()
		);
	}
}
