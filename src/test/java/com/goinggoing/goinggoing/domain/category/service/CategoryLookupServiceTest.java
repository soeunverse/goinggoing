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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryLookupServiceTest {

	private FakeRegionRepository regionRepository;
	private FakeThemeRepository themeRepository;
	private FakeSubThemeRepository subThemeRepository;
	private FakeTagRepository tagRepository;
	private CategoryLookupService categoryLookupService;

	@BeforeEach
	void setUp() {
		regionRepository = new FakeRegionRepository();
		themeRepository = new FakeThemeRepository();
		subThemeRepository = new FakeSubThemeRepository();
		tagRepository = new FakeTagRepository();
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
		regionRepository.save(new Region(1L, "11", "서울", "서울특별시", 2));
		regionRepository.save(new Region(2L, "25", "대전", "대전광역시", 1));

		List<CategoryItemResponse> responses = categoryLookupService.getRegions();

		assertThat(responses).extracting(CategoryItemResponse::name).containsExactly("대전", "서울");
	}

	@Test
	@DisplayName("테마 목록은 contentCount 0과 함께 displayOrder 순서로 조회한다")
	void getThemesSuccess() {
		themeRepository.save(new Theme(1L, "맛집", "먹으러 떠나는 여행", 2));
		themeRepository.save(new Theme(2L, "자연/바다", "바다와 자연", 1));

		List<ThemeResponse> responses = categoryLookupService.getThemes();

		assertThat(responses).extracting(ThemeResponse::name).containsExactly("자연/바다", "맛집");
		assertThat(responses).extracting(ThemeResponse::contentCount).containsExactly(0L, 0L);
	}

	@Test
	@DisplayName("테마별 하위 카테고리를 displayOrder 순서로 조회한다")
	void getSubThemesSuccess() {
		Theme theme = themeRepository.save(new Theme(1L, "맛집", "먹으러 떠나는 여행", 1));
		subThemeRepository.save(new SubTheme(1L, theme, "카페", "커피와 디저트", 2));
		subThemeRepository.save(new SubTheme(2L, theme, "빵지순례", "유명 빵집", 1));

		List<CategoryItemResponse> responses = categoryLookupService.getSubThemes(theme.getId());

		assertThat(responses).extracting(CategoryItemResponse::name).containsExactly("빵지순례", "카페");
	}

	@Test
	@DisplayName("존재하지 않는 테마의 하위 카테고리 조회는 CATEGORY_NOT_FOUND 예외가 발생한다")
	void getSubThemesThemeNotFoundFails() {
		assertThatThrownBy(() -> categoryLookupService.getSubThemes(999L))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND));
	}

	@Test
	@DisplayName("태그 목록을 displayOrder 순서로 조회한다")
	void getTagsSuccess() {
		tagRepository.save(new Tag(1L, "성심당", 2));
		tagRepository.save(new Tag(2L, "광안리", 1));

		List<CategoryItemResponse> responses = categoryLookupService.getTags();

		assertThat(responses).extracting(CategoryItemResponse::name).containsExactly("광안리", "성심당");
	}

	private static class FakeRegionRepository implements RegionRepository {

		private final List<Region> regions = new ArrayList<>();

		@Override
		public List<Region> findAllByOrderByDisplayOrderAscIdAsc() {
			return regions.stream()
					.sorted(Comparator.comparingInt(Region::getDisplayOrder).thenComparing(Region::getId))
					.toList();
		}

		Region save(Region region) {
			regions.add(region);
			return region;
		}
	}

	private static class FakeThemeRepository implements ThemeRepository {

		private final List<Theme> themes = new ArrayList<>();

		@Override
		public List<Theme> findAllByOrderByDisplayOrderAscIdAsc() {
			return themes.stream()
					.sorted(Comparator.comparingInt(Theme::getDisplayOrder).thenComparing(Theme::getId))
					.toList();
		}

		@Override
		public boolean existsById(Long id) {
			return themes.stream().anyMatch(theme -> theme.getId().equals(id));
		}

		Theme save(Theme theme) {
			themes.add(theme);
			return theme;
		}
	}

	private static class FakeSubThemeRepository implements SubThemeRepository {

		private final List<SubTheme> subThemes = new ArrayList<>();

		@Override
		public List<SubTheme> findByThemeIdOrderByDisplayOrderAscIdAsc(Long themeId) {
			return subThemes.stream()
					.filter(subTheme -> subTheme.getTheme().getId().equals(themeId))
					.sorted(Comparator.comparingInt(SubTheme::getDisplayOrder).thenComparing(SubTheme::getId))
					.toList();
		}

		SubTheme save(SubTheme subTheme) {
			subThemes.add(subTheme);
			return subTheme;
		}
	}

	private static class FakeTagRepository implements TagRepository {

		private final List<Tag> tags = new ArrayList<>();

		@Override
		public List<Tag> findAllByOrderByDisplayOrderAscIdAsc() {
			return tags.stream()
					.sorted(Comparator.comparingInt(Tag::getDisplayOrder).thenComparing(Tag::getId))
					.toList();
		}

		Tag save(Tag tag) {
			tags.add(tag);
			return tag;
		}
	}
}
