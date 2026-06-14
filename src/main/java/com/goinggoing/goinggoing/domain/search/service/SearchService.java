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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
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
		// 검색어 정규화
		String normalizedKeyword = normalizeKeyword(keyword);
		// 키워드 검색 실행
		List<Content> contents = contentRepository.searchByKeyword(normalizedKeyword);

		// 검색 로그 저장
		User user = findUserOrNull(userId);
		SearchLog searchLog = searchLogRepository.save(SearchLog.create(user, normalizedKeyword, contents.size()));
		log.info("[DB 저장] 검색 로그 저장 searchLogId={} userId={} keyword={} resultCount={}", searchLog == null ? null : searchLog.getId(), user == null ? null : user.getId(), normalizedKeyword, contents.size());

		// 검색 결과 응답 생성
		return contents.stream()
				.map(this::toSummaryResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<ContentSummaryResponse> searchByFilter(SearchFilterCondition condition) {
		// 태그 조건 정리
		List<Long> tagIds = condition.tagIds() == null || condition.tagIds().isEmpty() ? null : condition.tagIds();
		// 필터 검색 실행
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
		// 인기 검색어 조회
		return searchLogRepository.findPopularKeywords(KEYWORD_LIMIT);
	}

	@Transactional(readOnly = true)
	public List<RecentSearchKeywordResponse> getRecentKeywords(Long userId) {
		// 사용자 상태 검증
		validateActiveUser(userId);
		// 최근 검색어 조회
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
