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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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
		if (userId == null) {
			return hotFallback(FEED_LIMIT);
		}

		return userPreferenceRepository.findByUserId(userId)
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
	}

	public List<ContentSummaryResponse> getRelatedContents(Long contentId) {
		Content baseContent = contentRepository.findPublishedContent(contentId)
				.orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

		return contentRepository.findRelatedContents(
						baseContent.getId(),
						baseContent.getRegion().getId(),
						baseContent.getTheme() == null ? null : baseContent.getTheme().getId(),
						baseContent.getTags().stream().map(Tag::getId).toList()
				)
				.stream()
				.limit(RELATED_LIMIT)
				.map(this::toSummaryResponse)
				.toList();
	}

	public List<ContentSummaryResponse> getRouletteCandidates(RouletteRecommendationCondition condition) {
		List<Long> tagIds = condition.tagIds() == null || condition.tagIds().isEmpty() ? null : condition.tagIds();
		return contentRepository.findRouletteCandidates(
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
	}

	private List<ContentSummaryResponse> hotFallback(int limit) {
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
