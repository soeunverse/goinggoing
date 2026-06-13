package com.goinggoing.goinggoing.domain.category.service;

import com.goinggoing.goinggoing.domain.category.dto.CategoryItemResponse;
import com.goinggoing.goinggoing.domain.category.dto.ThemeResponse;
import com.goinggoing.goinggoing.domain.category.entity.Region;
import com.goinggoing.goinggoing.domain.category.entity.SubTheme;
import com.goinggoing.goinggoing.domain.category.entity.Tag;
import com.goinggoing.goinggoing.domain.category.entity.Theme;
import com.goinggoing.goinggoing.domain.category.repository.RegionRepository;
import com.goinggoing.goinggoing.domain.category.repository.SubThemeRepository;
import com.goinggoing.goinggoing.domain.category.repository.TagRepository;
import com.goinggoing.goinggoing.domain.category.repository.ThemeRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryLookupService {

	private final RegionRepository regionRepository;
	private final ThemeRepository themeRepository;
	private final SubThemeRepository subThemeRepository;
	private final TagRepository tagRepository;

	public CategoryLookupService(
			RegionRepository regionRepository,
			ThemeRepository themeRepository,
			SubThemeRepository subThemeRepository,
			TagRepository tagRepository
	) {
		this.regionRepository = regionRepository;
		this.themeRepository = themeRepository;
		this.subThemeRepository = subThemeRepository;
		this.tagRepository = tagRepository;
	}

	public List<CategoryItemResponse> getRegions() {
		return regionRepository.findAllByOrderByDisplayOrderAscIdAsc()
				.stream()
				.map(this::toRegionResponse)
				.toList();
	}

	public List<ThemeResponse> getThemes() {
		return themeRepository.findAllByOrderByDisplayOrderAscIdAsc()
				.stream()
				.map(this::toThemeResponse)
				.toList();
	}

	public List<CategoryItemResponse> getSubThemes(Long themeId) {
		if (!themeRepository.existsById(themeId)) {
			throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
		}

		return subThemeRepository.findByThemeIdOrderByDisplayOrderAscIdAsc(themeId)
				.stream()
				.map(this::toSubThemeResponse)
				.toList();
	}

	public List<CategoryItemResponse> getTags() {
		return tagRepository.findAllByOrderByDisplayOrderAscIdAsc()
				.stream()
				.map(this::toTagResponse)
				.toList();
	}

	private CategoryItemResponse toRegionResponse(Region region) {
		return new CategoryItemResponse(region.getId(), region.getName(), region.getDisplayOrder());
	}

	private ThemeResponse toThemeResponse(Theme theme) {
		return new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getDisplayOrder(), 0L);
	}

	private CategoryItemResponse toSubThemeResponse(SubTheme subTheme) {
		return new CategoryItemResponse(subTheme.getId(), subTheme.getName(), subTheme.getDisplayOrder());
	}

	private CategoryItemResponse toTagResponse(Tag tag) {
		return new CategoryItemResponse(tag.getId(), tag.getName(), tag.getDisplayOrder());
	}
}
