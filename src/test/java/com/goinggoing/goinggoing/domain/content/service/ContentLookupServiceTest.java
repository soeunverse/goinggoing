package com.goinggoing.goinggoing.domain.content.service;

import com.goinggoing.goinggoing.domain.category.entity.Region;
import com.goinggoing.goinggoing.domain.category.entity.SubTheme;
import com.goinggoing.goinggoing.domain.category.entity.Tag;
import com.goinggoing.goinggoing.domain.category.entity.Theme;
import com.goinggoing.goinggoing.domain.content.dto.ContentDetailResponse;
import com.goinggoing.goinggoing.domain.content.dto.ContentSummaryResponse;
import com.goinggoing.goinggoing.domain.content.entity.Content;
import com.goinggoing.goinggoing.domain.content.entity.ContentCard;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.content.repository.ContentRepository;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContentLookupServiceTest {

	private ContentRepository contentRepository;
	private ContentLookupService contentLookupService;

	@BeforeEach
	void setUp() {
		contentRepository = mock(ContentRepository.class);
		contentLookupService = new ContentLookupService(contentRepository);
	}

	@Test
	@DisplayName("컨텐츠 목록은 공개 컨텐츠를 HOT 점수 순서로 조회한다")
	void getContentsSuccess() {
		Content content = content("성심당", 120L, 30L, new BigDecimal("150.00"));
		when(contentRepository.findPublishedContents(3L, 1L, ContentType.RESTAURANT))
				.thenReturn(List.of(content));

		List<ContentSummaryResponse> responses = contentLookupService.getContents(3L, 1L, ContentType.RESTAURANT);

		assertThat(responses).hasSize(1);
		assertThat(responses.get(0).title()).isEqualTo("성심당");
		assertThat(responses.get(0).regionName()).isEqualTo("대전");
		assertThat(responses.get(0).themeName()).isEqualTo("맛집");
		assertThat(responses.get(0).hot()).isTrue();
	}

	@Test
	@DisplayName("컨텐츠 상세 조회는 카드뉴스, 태그를 반환하고 조회수를 증가시킨다")
	void getContentDetailSuccess() {
		Content content = content("성심당", 120L, 30L, new BigDecimal("150.00"));
		content.addCard(new ContentCard(1L, content, "대표 메뉴", "튀김소보로와 부추빵", "https://image.test/card.jpg", 1));
		content.addTag(new Tag(1L, "빵지순례", 1));
		when(contentRepository.findPublishedContent(1L)).thenReturn(Optional.of(content));

		ContentDetailResponse response = contentLookupService.getContentDetail(1L);

		assertThat(response.title()).isEqualTo("성심당");
		assertThat(response.viewCount()).isEqualTo(121L);
		assertThat(response.cards()).extracting(card -> card.title()).containsExactly("대표 메뉴");
		assertThat(response.tags()).extracting(tag -> tag.name()).containsExactly("빵지순례");
	}

	@Test
	@DisplayName("존재하지 않는 컨텐츠 상세 조회는 CONTENT_NOT_FOUND 예외가 발생한다")
	void getContentDetailNotFoundFails() {
		when(contentRepository.findPublishedContent(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> contentLookupService.getContentDetail(999L))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CONTENT_NOT_FOUND));
	}

	@Test
	@DisplayName("HOT 컨텐츠는 HOT 점수, 찜수, 조회수 순서로 조회한다")
	void getHotContentsSuccess() {
		Content content = content("광안리 해수욕장", 300L, 80L, new BigDecimal("380.00"));
		when(contentRepository.findHotContents()).thenReturn(List.of(content));

		List<ContentSummaryResponse> responses = contentLookupService.getHotContents();

		assertThat(responses).extracting(ContentSummaryResponse::title).containsExactly("광안리 해수욕장");
		assertThat(responses.get(0).hot()).isTrue();
	}

	private Content content(String title, Long viewCount, Long bookmarkCount, BigDecimal hotScore) {
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
				"당일치기로 가기 좋은 대표 컨텐츠",
				"카드뉴스로 보기 좋은 상세 설명",
				"대전 중구 대종로",
				new BigDecimal("36.32750"),
				new BigDecimal("127.42720"),
				"https://image.test/thumb.jpg",
				viewCount,
				bookmarkCount,
				hotScore,
				true
		);
	}
}
