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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CategoryLookupServiceTest {

	private RegionRepository regionRepository;
	private ThemeRepository themeRepository;
	private SubThemeRepository subThemeRepository;
	private TagRepository tagRepository;
	private CategoryLookupService categoryLookupService;

	@BeforeEach
	void setUp() {
		regionRepository = mock(RegionRepository.class);
		themeRepository = mock(ThemeRepository.class);
		subThemeRepository = mock(SubThemeRepository.class);
		tagRepository = mock(TagRepository.class);
		categoryLookupService = new CategoryLookupService(
				regionRepository,
				themeRepository,
				subThemeRepository,
				tagRepository
		);
	}

	@Test
	@DisplayName("지역 목록을 displayOrder 순서로 조회한다")
	void getRegionsSuccess() {
		when(regionRepository.findAllByOrderByDisplayOrderAscIdAsc()).thenReturn(List.of(
				new Region(2L, "25", "대전", "대전광역시", 1),
				new Region(1L, "11", "서울", "서울특별시", 2)
		));

		List<CategoryItemResponse> responses = categoryLookupService.getRegions();

		assertThat(responses).extracting(CategoryItemResponse::name).containsExactly("대전", "서울");
	}

	@Test
	@DisplayName("테마 목록은 contentCount 0과 함께 displayOrder 순서로 조회한다")
	void getThemesSuccess() {
		when(themeRepository.findAllByOrderByDisplayOrderAscIdAsc()).thenReturn(List.of(
				new Theme(2L, "자연/바다", "바다와 자연", 1),
				new Theme(1L, "맛집", "먹으러 떠나는 여행", 2)
		));

		List<ThemeResponse> responses = categoryLookupService.getThemes();

		assertThat(responses).extracting(ThemeResponse::name).containsExactly("자연/바다", "맛집");
		assertThat(responses).extracting(ThemeResponse::contentCount).containsExactly(0L, 0L);
	}

	@Test
	@DisplayName("테마별 하위 카테고리를 displayOrder 순서로 조회한다")
	void getSubThemesSuccess() {
		Theme theme = new Theme(1L, "맛집", "먹으러 떠나는 여행", 1);
		when(themeRepository.existsById(theme.getId())).thenReturn(true);
		when(subThemeRepository.findByThemeIdOrderByDisplayOrderAscIdAsc(theme.getId())).thenReturn(List.of(
				new SubTheme(2L, theme, "빵지순례", "유명 빵집", 1),
				new SubTheme(1L, theme, "카페", "커피와 디저트", 2)
		));

		List<CategoryItemResponse> responses = categoryLookupService.getSubThemes(theme.getId());

		assertThat(responses).extracting(CategoryItemResponse::name).containsExactly("빵지순례", "카페");
	}

	@Test
	@DisplayName("존재하지 않는 테마의 하위 카테고리 조회는 CATEGORY_NOT_FOUND 예외가 발생한다")
	void getSubThemesThemeNotFoundFails() {
		when(themeRepository.existsById(999L)).thenReturn(false);

		assertThatThrownBy(() -> categoryLookupService.getSubThemes(999L))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND));
	}

	@Test
	@DisplayName("태그 목록을 displayOrder 순서로 조회한다")
	void getTagsSuccess() {
		when(tagRepository.findAllByOrderByDisplayOrderAscIdAsc()).thenReturn(List.of(
				new Tag(2L, "광안리", 1),
				new Tag(1L, "성심당", 2)
		));

		List<CategoryItemResponse> responses = categoryLookupService.getTags();

		assertThat(responses).extracting(CategoryItemResponse::name).containsExactly("광안리", "성심당");
	}
}
