package com.goinggoing.goinggoing.domain.recommendation.service;

import com.goinggoing.goinggoing.domain.category.entity.Tag;
import com.goinggoing.goinggoing.domain.content.dto.ContentSummaryResponse;
import com.goinggoing.goinggoing.domain.content.entity.Content;
import com.goinggoing.goinggoing.domain.content.repository.ContentRepository;
import com.goinggoing.goinggoing.domain.recommendation.dto.RouletteRecommendationCondition;
import com.goinggoing.goinggoing.domain.user.entity.UserPreference;
import com.goinggoing.goinggoing.domain.user.repository.UserPreferenceRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RecommendationService {

	private static final int FEED_LIMIT = 20;
	private static final int RELATED_LIMIT = 6;
	private static final int ROULETTE_LIMIT = 8;
	private static final BigDecimal HOT_THRESHOLD = BigDecimal.ZERO;

	private final ContentRepository contentRepository;
	private final UserPreferenceRepository userPreferenceRepository;

	public RecommendationService(ContentRepository contentRepository, UserPreferenceRepository userPreferenceRepository) {
		this.contentRepository = contentRepository;
		this.userPreferenceRepository = userPreferenceRepository;
	}

	public List<ContentSummaryResponse> getFeed(Long userId) {
		log.info("[추천 요청] 피드 userId={}", userId);
		// 비로그인 HOT 기본 추천
		if (userId == null) {
			List<ContentSummaryResponse> response = hotFallback(FEED_LIMIT);
			log.info("[추천 응답] 피드 fallback=HOT count={}", response.size());
			return response;
		}

		// 취향 기반 피드 추천
		List<ContentSummaryResponse> response = userPreferenceRepository.findByUserId(userId)
				.filter(this::hasAnyPreference)
				.map(preference -> contentRepository.findRecommendedFeed(
						preference.getRegionIds(),
						preference.getThemeIds(),
						preference.getTagIds()
				))
				.filter(contents -> !contents.isEmpty())
				.orElseGet(contentRepository::findHotContents)
				.stream()
				.limit(FEED_LIMIT)
				.map(this::toSummaryResponse)
				.toList();
		log.info("[추천 응답] 피드 userId={} count={}", userId, response.size());
		return response;
	}

	public List<ContentSummaryResponse> getRelatedContents(Long contentId) {
		log.info("[추천 요청] 연관 컨텐츠 contentId={}", contentId);
		// 기준 컨텐츠 조회
		Content baseContent = contentRepository.findPublishedContent(contentId)
				.orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

		// 연관 컨텐츠 추천
		List<ContentSummaryResponse> response = contentRepository.findRelatedContents(
						baseContent.getId(),
						baseContent.getRegion().getId(),
						baseContent.getTheme() == null ? null : baseContent.getTheme().getId(),
						baseContent.getTags().stream().map(Tag::getId).toList()
				)
				.stream()
				.limit(RELATED_LIMIT)
				.map(this::toSummaryResponse)
				.toList();
		log.info("[추천 응답] 연관 컨텐츠 contentId={} count={}", contentId, response.size());
		return response;
	}

	public List<ContentSummaryResponse> getRouletteCandidates(RouletteRecommendationCondition condition) {
		// 태그 조건 정리
		List<Long> tagIds = condition.tagIds() == null || condition.tagIds().isEmpty() ? null : condition.tagIds();
		log.info(
				"[추천 요청] 룰렛 후보 regionId={} themeId={} subThemeId={} contentType={} tagIds={}",
				condition.regionId(),
				condition.themeId(),
				condition.subThemeId(),
				condition.contentType(),
				tagIds
		);
		// 룰렛 후보 추천
		List<ContentSummaryResponse> response = contentRepository.findRouletteCandidates(
						condition.regionId(),
						condition.themeId(),
						condition.subThemeId(),
						condition.contentType(),
						tagIds
				)
				.stream()
				.limit(ROULETTE_LIMIT)
				.map(this::toSummaryResponse)
				.toList();
		log.info("[추천 응답] 룰렛 후보 count={}", response.size());
		return response;
	}

	private List<ContentSummaryResponse> hotFallback(int limit) {
		// HOT 기본 추천 응답 생성
		return contentRepository.findHotContents()
				.stream()
				.limit(limit)
				.map(this::toSummaryResponse)
				.toList();
	}

	private boolean hasAnyPreference(UserPreference preference) {
		return !preference.getRegionIds().isEmpty()
				|| !preference.getThemeIds().isEmpty()
				|| !preference.getTagIds().isEmpty();
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
