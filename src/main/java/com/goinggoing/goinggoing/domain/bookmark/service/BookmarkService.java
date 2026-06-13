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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
		validateActiveUser(userId);

		return bookmarkRepository.findAllByUserId(userId)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional
	public BookmarkResponse addBookmark(Long userId, Long contentId) {
		User user = validateActiveUser(userId);
		Content content = contentRepository.findPublishedContent(contentId)
				.orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

		if (bookmarkRepository.existsByUserIdAndContentId(userId, contentId)) {
			throw new BusinessException(ErrorCode.BOOKMARK_ALREADY_EXISTS);
		}

		content.increaseBookmarkCount();
		Bookmark bookmark = bookmarkRepository.save(Bookmark.create(user, content));

		return toResponse(bookmark);
	}

	@Transactional
	public void deleteBookmark(Long userId, Long bookmarkId) {
		validateActiveUser(userId);
		Bookmark bookmark = bookmarkRepository.findByIdAndUserId(bookmarkId, userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.BOOKMARK_NOT_FOUND));

		bookmark.getContent().decreaseBookmarkCount();
		bookmarkRepository.delete(bookmark);
	}

	@Transactional
	public void deleteBookmarks(Long userId, List<Long> bookmarkIds) {
		validateActiveUser(userId);
		List<Bookmark> bookmarks = bookmarkRepository.findAllByIdInAndUserId(bookmarkIds, userId);

		bookmarks.forEach(bookmark -> bookmark.getContent().decreaseBookmarkCount());
		bookmarkRepository.deleteAll(bookmarks);
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
