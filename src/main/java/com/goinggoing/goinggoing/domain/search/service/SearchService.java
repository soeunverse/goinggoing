package com.goinggoing.goinggoing.domain.search.service;

import com.goinggoing.goinggoing.domain.category.entity.Tag;
import com.goinggoing.goinggoing.domain.content.dto.ContentSummaryResponse;
import com.goinggoing.goinggoing.domain.content.entity.Content;
import com.goinggoing.goinggoing.domain.content.repository.ContentRepository;
import com.goinggoing.goinggoing.domain.search.dto.PopularSearchKeywordResponse;
import com.goinggoing.goinggoing.domain.search.dto.RecentSearchKeywordResponse;
import com.goinggoing.goinggoing.domain.search.dto.SearchFilterCondition;
import com.goinggoing.goinggoing.domain.search.entity.SearchLog;
import com.goinggoing.goinggoing.domain.search.repository.SearchLogRepository;
import com.goinggoing.goinggoing.domain.user.entity.User;
import com.goinggoing.goinggoing.domain.user.repository.UserRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SearchService {

	private static final int KEYWORD_LIMIT = 10;
	private static final BigDecimal HOT_THRESHOLD = BigDecimal.ZERO;

	private final ContentRepository contentRepository;
	private final SearchLogRepository searchLogRepository;
	private final UserRepository userRepository;

	public SearchService(
			ContentRepository contentRepository,
			SearchLogRepository searchLogRepository,
			UserRepository userRepository
	) {
		this.contentRepository = contentRepository;
		this.searchLogRepository = searchLogRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public List<ContentSummaryResponse> searchByKeyword(String keyword, Long userId) {
		String normalizedKeyword = normalizeKeyword(keyword);
		List<Content> contents = contentRepository.searchByKeyword(normalizedKeyword);

		User user = findUserOrNull(userId);
		searchLogRepository.save(SearchLog.create(user, normalizedKeyword, contents.size()));

		return contents.stream()
				.map(this::toSummaryResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<ContentSummaryResponse> searchByFilter(SearchFilterCondition condition) {
		List<Long> tagIds = condition.tagIds() == null || condition.tagIds().isEmpty() ? null : condition.tagIds();
		return contentRepository.searchByFilter(
						condition.regionId(),
						condition.themeId(),
						condition.subThemeId(),
						condition.contentType(),
						tagIds
				)
				.stream()
				.map(this::toSummaryResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<PopularSearchKeywordResponse> getPopularKeywords() {
		return searchLogRepository.findPopularKeywords(KEYWORD_LIMIT);
	}

	@Transactional(readOnly = true)
	public List<RecentSearchKeywordResponse> getRecentKeywords(Long userId) {
		validateActiveUser(userId);
		return searchLogRepository.findRecentKeywordsByUserId(userId, KEYWORD_LIMIT);
	}

	private String normalizeKeyword(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			throw new BusinessException(ErrorCode.BAD_REQUEST);
		}
		return keyword.trim();
	}

	private User findUserOrNull(Long userId) {
		if (userId == null) {
			return null;
		}
		return validateActiveUser(userId);
	}

	private User validateActiveUser(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.UNAUTHORIZED);
		}
		return user;
	}

	private ContentSummaryResponse toSummaryResponse(Content content) {
		return new ContentSummaryResponse(
				content.getId(),
				content.getTitle(),
				content.getSummary(),
				content.getRegion().getName(),
				content.getTheme() == null ? null : content.getTheme().getName(),
				content.getThumbnailUrl(),
				content.getContentType(),
				content.getViewCount(),
				content.getBookmarkCount(),
				isHot(content)
		);
	}

	private boolean isHot(Content content) {
		return content.getHotScore().compareTo(HOT_THRESHOLD) > 0;
	}
}
