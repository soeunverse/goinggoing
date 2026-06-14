package com.goinggoing.goinggoing.domain.content.service;

import com.goinggoing.goinggoing.domain.category.entity.Region;
import com.goinggoing.goinggoing.domain.category.entity.SubTheme;
import com.goinggoing.goinggoing.domain.category.entity.Tag;
import com.goinggoing.goinggoing.domain.category.entity.Theme;
import com.goinggoing.goinggoing.domain.category.repository.RegionRepository;
import com.goinggoing.goinggoing.domain.category.repository.SubThemeRepository;
import com.goinggoing.goinggoing.domain.category.repository.TagRepository;
import com.goinggoing.goinggoing.domain.category.repository.ThemeRepository;
import com.goinggoing.goinggoing.domain.content.dto.ContentCardResponse;
import com.goinggoing.goinggoing.domain.content.dto.ContentDetailResponse;
import com.goinggoing.goinggoing.domain.content.dto.ContentManagementCardRequest;
import com.goinggoing.goinggoing.domain.content.dto.ContentManagementRequest;
import com.goinggoing.goinggoing.domain.content.dto.ContentTagResponse;
import com.goinggoing.goinggoing.domain.content.entity.Content;
import com.goinggoing.goinggoing.domain.content.entity.ContentCard;
import com.goinggoing.goinggoing.domain.content.repository.ContentRepository;
import com.goinggoing.goinggoing.domain.user.entity.User;
import com.goinggoing.goinggoing.domain.user.repository.UserRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContentManagementService {

	private final UserRepository userRepository;
	private final ContentRepository contentRepository;
	private final RegionRepository regionRepository;
	private final ThemeRepository themeRepository;
	private final SubThemeRepository subThemeRepository;
	private final TagRepository tagRepository;

	public ContentManagementService(
			UserRepository userRepository,
			ContentRepository contentRepository,
			RegionRepository regionRepository,
			ThemeRepository themeRepository,
			SubThemeRepository subThemeRepository,
			TagRepository tagRepository
	) {
		this.userRepository = userRepository;
		this.contentRepository = contentRepository;
		this.regionRepository = regionRepository;
		this.themeRepository = themeRepository;
		this.subThemeRepository = subThemeRepository;
		this.tagRepository = tagRepository;
	}

	@Transactional
	public ContentDetailResponse createContent(Long adminUserId, ContentManagementRequest request) {
		// 관리자 권한 검증
		validateAdmin(adminUserId);
		// 카테고리 값 조회
		CategoryValues categoryValues = resolveCategories(request);
		// 태그 값 조회
		List<Tag> tags = resolveTags(request.tagIds());

		// 관리자 컨텐츠 생성
		Content content = Content.createAdminContent(
				categoryValues.region(),
				categoryValues.theme(),
				categoryValues.subTheme(),
				request.title(),
				request.contentType(),
				request.summary(),
				request.description(),
				request.address(),
				request.latitude(),
				request.longitude(),
				request.thumbnailUrl(),
				request.published()
		);
		// 카드와 태그 연결
		content.replaceCards(toCards(content, request.cards()));
		content.replaceTags(tags);

		return toDetailResponse(contentRepository.save(content));
	}

	@Transactional
	public ContentDetailResponse updateContent(Long adminUserId, Long contentId, ContentManagementRequest request) {
		// 관리자 권한 검증
		validateAdmin(adminUserId);
		// 컨텐츠 조회
		Content content = contentRepository.findById(contentId)
				.orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));
		// 카테고리 값 조회
		CategoryValues categoryValues = resolveCategories(request);
		// 태그 값 조회
		List<Tag> tags = resolveTags(request.tagIds());

		// 컨텐츠 정보 수정
		content.update(
				categoryValues.region(),
				categoryValues.theme(),
				categoryValues.subTheme(),
				request.title(),
				request.contentType(),
				request.summary(),
				request.description(),
				request.address(),
				request.latitude(),
				request.longitude(),
				request.thumbnailUrl(),
				request.published()
		);
		// 카드와 태그 전체 교체
		content.replaceCards(toCards(content, request.cards()));
		content.replaceTags(tags);

		return toDetailResponse(content);
	}

	@Transactional
	public void deleteContent(Long adminUserId, Long contentId) {
		// 관리자 권한 검증
		validateAdmin(adminUserId);
		// 컨텐츠 조회
		Content content = contentRepository.findById(contentId)
				.orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));
		// soft delete 처리
		content.softDelete(LocalDateTime.now());
	}

	private void validateAdmin(Long adminUserId) {
		User user = userRepository.findById(adminUserId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.UNAUTHORIZED);
		}
		if (!user.isAdmin()) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}
	}

	private CategoryValues resolveCategories(ContentManagementRequest request) {
		Region region = regionRepository.findById(request.regionId())
				.orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
		Theme theme = request.themeId() == null ? null : themeRepository.findById(request.themeId())
				.orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
		SubTheme subTheme = request.subThemeId() == null ? null : subThemeRepository.findById(request.subThemeId())
				.orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
		return new CategoryValues(region, theme, subTheme);
	}

	private List<Tag> resolveTags(List<Long> tagIds) {
		if (tagIds == null || tagIds.isEmpty()) {
			return List.of();
		}
		List<Tag> tags = tagRepository.findAllById(tagIds);
		if (tags.size() != tagIds.size()) {
			throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
		}
		return tags;
	}

	private List<ContentCard> toCards(Content content, List<ContentManagementCardRequest> cards) {
		if (cards == null) {
			return List.of();
		}
		return cards.stream()
				.map(card -> new ContentCard(null, content, card.title(), card.body(), card.imageUrl(), card.displayOrder()))
				.toList();
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
				content.getHotScore().signum() > 0,
				content.getCards().stream().map(this::toCardResponse).toList(),
				content.getTags().stream().map(this::toTagResponse).toList()
		);
	}

	private ContentCardResponse toCardResponse(ContentCard card) {
		return new ContentCardResponse(card.getId(), card.getTitle(), card.getBody(), card.getImageUrl(), card.getDisplayOrder());
	}

	private ContentTagResponse toTagResponse(Tag tag) {
		return new ContentTagResponse(tag.getId(), tag.getName());
	}

	private record CategoryValues(Region region, Theme theme, SubTheme subTheme) {
	}
}
