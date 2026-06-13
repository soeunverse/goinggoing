package com.goinggoing.goinggoing.domain.content.service;

import com.goinggoing.goinggoing.domain.category.entity.Region;
import com.goinggoing.goinggoing.domain.category.entity.SubTheme;
import com.goinggoing.goinggoing.domain.category.entity.Tag;
import com.goinggoing.goinggoing.domain.category.entity.Theme;
import com.goinggoing.goinggoing.domain.category.repository.RegionRepository;
import com.goinggoing.goinggoing.domain.category.repository.SubThemeRepository;
import com.goinggoing.goinggoing.domain.category.repository.TagRepository;
import com.goinggoing.goinggoing.domain.category.repository.ThemeRepository;
import com.goinggoing.goinggoing.domain.content.dto.ContentManagementCardRequest;
import com.goinggoing.goinggoing.domain.content.dto.ContentManagementRequest;
import com.goinggoing.goinggoing.domain.content.dto.ContentDetailResponse;
import com.goinggoing.goinggoing.domain.content.entity.Content;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.content.repository.ContentRepository;
import com.goinggoing.goinggoing.domain.user.entity.User;
import com.goinggoing.goinggoing.domain.user.entity.UserRole;
import com.goinggoing.goinggoing.domain.user.repository.UserRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContentManagementServiceTest {

	private UserRepository userRepository;
	private ContentRepository contentRepository;
	private RegionRepository regionRepository;
	private ThemeRepository themeRepository;
	private SubThemeRepository subThemeRepository;
	private TagRepository tagRepository;
	private ContentManagementService contentManagementService;

	@BeforeEach
	void setUp() {
		userRepository = mock(UserRepository.class);
		contentRepository = mock(ContentRepository.class);
		regionRepository = mock(RegionRepository.class);
		themeRepository = mock(ThemeRepository.class);
		subThemeRepository = mock(SubThemeRepository.class);
		tagRepository = mock(TagRepository.class);
		contentManagementService = new ContentManagementService(
				userRepository,
				contentRepository,
				regionRepository,
				themeRepository,
				subThemeRepository,
				tagRepository
		);
	}

	@Test
	@DisplayName("ADMIN 사용자는 컨텐츠를 생성한다")
	void createContentSuccess() {
		stubAdmin();
		stubCategories();
		when(contentRepository.save(any(Content.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ContentDetailResponse response = contentManagementService.createContent(1L, request("성심당"));

		assertThat(response.title()).isEqualTo("성심당");
		assertThat(response.cards()).extracting(card -> card.title()).containsExactly("대표 메뉴");
		assertThat(response.tags()).extracting(tag -> tag.name()).containsExactly("빵지순례");
	}

	@Test
	@DisplayName("ADMIN 사용자가 아니면 컨텐츠 생성은 FORBIDDEN 예외가 발생한다")
	void createContentForbiddenFails() {
		User user = User.create("user@example.com", "encoded-password", "일반유저").withId(1L);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		assertThatThrownBy(() -> contentManagementService.createContent(1L, request("성심당")))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN));
	}

	@Test
	@DisplayName("ADMIN 사용자는 컨텐츠를 수정하고 카드와 태그를 전체 교체한다")
	void updateContentSuccess() {
		stubAdmin();
		stubCategories();
		Content content = content("성심당");
		when(contentRepository.findById(1L)).thenReturn(Optional.of(content));

		ContentDetailResponse response = contentManagementService.updateContent(1L, 1L, request("성심당 업데이트"));

		assertThat(response.title()).isEqualTo("성심당 업데이트");
		assertThat(response.cards()).extracting(card -> card.title()).containsExactly("대표 메뉴");
		assertThat(response.tags()).extracting(tag -> tag.name()).containsExactly("빵지순례");
	}

	@Test
	@DisplayName("ADMIN 사용자는 컨텐츠를 soft delete 처리한다")
	void deleteContentSuccess() {
		stubAdmin();
		Content content = content("성심당");
		when(contentRepository.findById(1L)).thenReturn(Optional.of(content));

		contentManagementService.deleteContent(1L, 1L);

		assertThat(content.isPublished()).isFalse();
		assertThat(content.getDeletedAt()).isNotNull();
	}

	private void stubAdmin() {
		User admin = User.create("admin@example.com", "encoded-password", "관리자")
				.withId(1L)
				.withRole(UserRole.ADMIN);
		when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
	}

	private void stubCategories() {
		Region region = new Region(3L, "3", "대전", "대전광역시", 3);
		Theme theme = new Theme(1L, "맛집", "먹으러 떠나는 여행", 1);
		SubTheme subTheme = new SubTheme(1L, theme, "빵지순례", "유명 빵집", 1);
		Tag tag = new Tag(1L, "빵지순례", 1);
		when(regionRepository.findById(3L)).thenReturn(Optional.of(region));
		when(themeRepository.findById(1L)).thenReturn(Optional.of(theme));
		when(subThemeRepository.findById(1L)).thenReturn(Optional.of(subTheme));
		when(tagRepository.findAllById(List.of(1L))).thenReturn(List.of(tag));
	}

	private ContentManagementRequest request(String title) {
		return new ContentManagementRequest(
				3L,
				1L,
				1L,
				title,
				ContentType.RESTAURANT,
				"대전 당일치기 대표 컨텐츠",
				"카드뉴스 설명",
				"대전 중구 대종로",
				new BigDecimal("36.32750"),
				new BigDecimal("127.42720"),
				"https://image.test/thumb.jpg",
				true,
				List.of(1L),
				List.of(new ContentManagementCardRequest("대표 메뉴", "튀김소보로와 부추빵", "https://image.test/card.jpg", 1))
		);
	}

	private Content content(String title) {
		Region region = new Region(3L, "3", "대전", "대전광역시", 3);
		Theme theme = new Theme(1L, "맛집", "먹으러 떠나는 여행", 1);
		SubTheme subTheme = new SubTheme(1L, theme, "빵지순례", "유명 빵집", 1);
		return new Content(
				1L,
				region,
				theme,
				subTheme,
				title,
				ContentType.RESTAURANT,
				"대전 당일치기 대표 컨텐츠",
				"카드뉴스 설명",
				"대전 중구 대종로",
				new BigDecimal("36.32750"),
				new BigDecimal("127.42720"),
				"https://image.test/thumb.jpg",
				120L,
				30L,
				new BigDecimal("150.00"),
				true
		);
	}
}
