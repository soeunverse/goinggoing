package com.goinggoing.goinggoing.domain.route.service;

import com.goinggoing.goinggoing.domain.category.entity.Region;
import com.goinggoing.goinggoing.domain.category.entity.Theme;
import com.goinggoing.goinggoing.domain.content.entity.Content;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.content.repository.ContentRepository;
import com.goinggoing.goinggoing.domain.route.dto.RouteGenerateRequest;
import com.goinggoing.goinggoing.domain.route.dto.RouteResponse;
import com.goinggoing.goinggoing.domain.route.entity.Route;
import com.goinggoing.goinggoing.domain.route.entity.RoutePlace;
import com.goinggoing.goinggoing.domain.route.entity.RouteStatus;
import com.goinggoing.goinggoing.domain.route.repository.RouteRepository;
import com.goinggoing.goinggoing.domain.user.entity.TripDurationType;
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

class RouteServiceTest {

	private RouteRepository routeRepository;
	private ContentRepository contentRepository;
	private RouteService routeService;

	@BeforeEach
	void setUp() {
		routeRepository = mock(RouteRepository.class);
		contentRepository = mock(ContentRepository.class);
		routeService = new RouteService(routeRepository, contentRepository);
	}

	@Test
	@DisplayName("루트 생성은 선택 컨텐츠와 여행 기간에 맞는 공개 루트를 반환한다")
	void generateRouteSuccess() {
		Route route = route("성심당 당일치기 루트", TripDurationType.DAY_TRIP);
		when(contentRepository.findPublishedContent(1L)).thenReturn(Optional.of(content()));
		when(routeRepository.findFirstByContentIdAndTripDurationType(1L, TripDurationType.DAY_TRIP))
				.thenReturn(Optional.of(route));

		RouteResponse response = routeService.generateRoute(new RouteGenerateRequest(1L, TripDurationType.DAY_TRIP));

		assertThat(response.title()).isEqualTo("성심당 당일치기 루트");
		assertThat(response.places()).extracting(place -> place.name()).containsExactly("성심당 본점");
	}

	@Test
	@DisplayName("루트 생성에서 컨텐츠가 없으면 CONTENT_NOT_FOUND 예외가 발생한다")
	void generateRouteContentNotFoundFails() {
		when(contentRepository.findPublishedContent(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> routeService.generateRoute(new RouteGenerateRequest(999L, TripDurationType.DAY_TRIP)))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CONTENT_NOT_FOUND));
	}

	@Test
	@DisplayName("루트 상세 조회는 장소 목록을 방문 순서대로 반환한다")
	void getRouteDetailSuccess() {
		when(routeRepository.findPublishedRoute(1L)).thenReturn(Optional.of(route("성심당 당일치기 루트", TripDurationType.DAY_TRIP)));

		RouteResponse response = routeService.getRouteDetail(1L);

		assertThat(response.routeId()).isEqualTo(1L);
		assertThat(response.places()).extracting(place -> place.visitOrder()).containsExactly(1);
	}

	@Test
	@DisplayName("존재하지 않는 루트 상세 조회는 ROUTE_NOT_FOUND 예외가 발생한다")
	void getRouteDetailNotFoundFails() {
		when(routeRepository.findPublishedRoute(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> routeService.getRouteDetail(999L))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ROUTE_NOT_FOUND));
	}

	@Test
	@DisplayName("지역별 루트 목록은 공개 루트를 반환한다")
	void getRoutesByRegionSuccess() {
		when(routeRepository.findPublishedRoutesByRegionId(3L)).thenReturn(List.of(route("성심당 당일치기 루트", TripDurationType.DAY_TRIP)));

		List<RouteResponse> responses = routeService.getRoutesByRegion(3L);

		assertThat(responses).extracting(RouteResponse::title).containsExactly("성심당 당일치기 루트");
	}

	private Content content() {
		Region region = new Region(3L, "3", "대전", "대전광역시", 3);
		Theme theme = new Theme(1L, "맛집", "먹으러 떠나는 여행", 1);
		return new Content(
				1L,
				region,
				theme,
				null,
				"성심당",
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

	private Route route(String title, TripDurationType tripDurationType) {
		Content content = content();
		Route route = new Route(
				1L,
				content,
				content.getTheme(),
				title,
				"대전 원도심을 가볍게 도는 루트",
				tripDurationType,
				RouteStatus.PUBLISHED,
				3200,
				180,
				new BigDecimal("36.32750"),
				new BigDecimal("127.42720")
		);
		route.addPlace(new RoutePlace(
				1L,
				route,
				content,
				"성심당 본점",
				ContentType.RESTAURANT,
				"대전 중구 대종로480번길 15",
				new BigDecimal("36.32750"),
				new BigDecimal("127.42720"),
				1,
				1,
				60,
				null,
				"대표 빵집에서 여행 시작"
		));
		return route;
	}
}
