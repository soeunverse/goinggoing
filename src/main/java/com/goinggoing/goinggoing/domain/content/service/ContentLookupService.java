package com.goinggoing.goinggoing.domain.content.service;

import com.goinggoing.goinggoing.domain.category.entity.Tag;
import com.goinggoing.goinggoing.domain.content.dto.ContentCardResponse;
import com.goinggoing.goinggoing.domain.content.dto.ContentDetailResponse;
import com.goinggoing.goinggoing.domain.content.dto.ContentSummaryResponse;
import com.goinggoing.goinggoing.domain.content.dto.ContentTagResponse;
import com.goinggoing.goinggoing.domain.content.entity.Content;
import com.goinggoing.goinggoing.domain.content.entity.ContentCard;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.content.repository.ContentRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ContentLookupService {

	private static final int HOT_CONTENT_LIMIT = 10;
	private static final BigDecimal HOT_THRESHOLD = BigDecimal.ZERO;

	private final ContentRepository contentRepository;

	public ContentLookupService(ContentRepository contentRepository) {
		this.contentRepository = contentRepository;
	}

	@Transactional(readOnly = true)
	public List<ContentSummaryResponse> getContents(Long regionId, Long themeId, ContentType contentType) {
		// 컨텐츠 카드 응답 생성
		return contentRepository.findPublishedContents(regionId, themeId, contentType)
				.stream()
				.map(this::toSummaryResponse)
				.toList();
	}

	@Transactional
	public ContentDetailResponse getContentDetail(Long contentId) {
		// 공개 컨텐츠 조회
		Content content = contentRepository.findPublishedContent(contentId)
				.orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

		// 조회수 증가
		content.increaseViewCount();

		// 상세 응답 생성
		return toDetailResponse(content);
	}

	@Transactional(readOnly = true)
	public List<ContentSummaryResponse> getHotContents() {
		// HOT 컨텐츠 응답 생성
		return contentRepository.findHotContents()
				.stream()
				.limit(HOT_CONTENT_LIMIT)
				.map(this::toSummaryResponse)
				.toList();
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

	private ContentDetailResponse toDetailResponse(Content content) {
		return new ContentDetailResponse(
				content.getId(),
				content.getTitle(),
				content.getSummary(),
				content.getDescription(),
				content.getAddress(),
				content.getLatitude(),
				content.getLongitude(),
				content.getThumbnailUrl(),
				content.getContentType(),
				content.getRegion().getName(),
				content.getTheme() == null ? null : content.getTheme().getName(),
				content.getSubTheme() == null ? null : content.getSubTheme().getName(),
				content.getViewCount(),
				content.getBookmarkCount(),
				isHot(content),
				content.getCards().stream().map(this::toCardResponse).toList(),
				content.getTags().stream().map(this::toTagResponse).toList()
		);
	}

	private ContentCardResponse toCardResponse(ContentCard card) {
		return new ContentCardResponse(
				card.getId(),
				card.getTitle(),
				card.getBody(),
				card.getImageUrl(),
				card.getDisplayOrder()
		);
	}

	private ContentTagResponse toTagResponse(Tag tag) {
		return new ContentTagResponse(tag.getId(), tag.getName());
	}

	private boolean isHot(Content content) {
		return content.getHotScore().compareTo(HOT_THRESHOLD) > 0;
	}
}
