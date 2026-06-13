package com.goinggoing.goinggoing.domain.route.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.route.dto.RouteGenerateRequest;
import com.goinggoing.goinggoing.domain.route.dto.RoutePlaceResponse;
import com.goinggoing.goinggoing.domain.route.dto.RouteResponse;
import com.goinggoing.goinggoing.domain.route.service.RouteService;
import com.goinggoing.goinggoing.domain.user.entity.TripDurationType;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import com.goinggoing.goinggoing.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RouteControllerTest {

	private final RouteService routeService = mock(RouteService.class);
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new RouteController(routeService))
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();

	@Test
	@DisplayName("루트 생성 API는 200과 루트 상세를 반환한다")
	void generateRouteSuccess() throws Exception {
		RouteGenerateRequest request = new RouteGenerateRequest(1L, TripDurationType.DAY_TRIP);
		when(routeService.generateRoute(request)).thenReturn(route());

		mockMvc.perform(post("/api/routes/generate")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.title").value("성심당 당일치기 루트"))
				.andExpect(jsonPath("$.data.places[0].name").value("성심당 본점"))
				.andExpect(jsonPath("$.message").value("루트 생성이 완료되었습니다."))
				.andExpect(jsonPath("$.errorCode").value(nullValue()));
	}

	@Test
	@DisplayName("루트 상세 조회 API는 200과 루트 상세를 반환한다")
	void getRouteDetailSuccess() throws Exception {
		when(routeService.getRouteDetail(1L)).thenReturn(route());

		mockMvc.perform(get("/api/routes/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.routeId").value(1L))
				.andExpect(jsonPath("$.message").value("루트 상세 조회가 완료되었습니다."));
	}

	@Test
	@DisplayName("존재하지 않는 루트 상세 조회는 404와 ROUTE_NOT_FOUND 응답을 반환한다")
	void getRouteDetailNotFoundFails() throws Exception {
		when(routeService.getRouteDetail(999L)).thenThrow(new BusinessException(ErrorCode.ROUTE_NOT_FOUND));

		mockMvc.perform(get("/api/routes/999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("ROUTE_NOT_FOUND"));
	}

	@Test
	@DisplayName("지역별 루트 조회 API는 regionId 기준으로 200과 루트 목록을 반환한다")
	void getRoutesByRegionSuccess() throws Exception {
		when(routeService.getRoutesByRegion(3L)).thenReturn(List.of(route()));

		mockMvc.perform(get("/api/routes").param("regionId", "3"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].title").value("성심당 당일치기 루트"))
				.andExpect(jsonPath("$.message").value("지역별 루트 조회가 완료되었습니다."));
	}

	private RouteResponse route() {
		return new RouteResponse(
				1L,
				1L,
				"성심당 당일치기 루트",
				"대전 원도심을 가볍게 도는 루트",
				TripDurationType.DAY_TRIP,
				3200,
				180,
				new BigDecimal("36.32750"),
				new BigDecimal("127.42720"),
				List.of(new RoutePlaceResponse(
						1L,
						1L,
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
				))
		);
	}
}
