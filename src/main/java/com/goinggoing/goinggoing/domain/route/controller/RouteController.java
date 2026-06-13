package com.goinggoing.goinggoing.domain.route.controller;

import com.goinggoing.goinggoing.domain.route.dto.RouteGenerateRequest;
import com.goinggoing.goinggoing.domain.route.dto.RouteResponse;
import com.goinggoing.goinggoing.domain.route.service.RouteService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@Tag(name = "Route", description = "당일치기 및 1박 2일 루트 API")
public class RouteController {

	private final RouteService routeService;

	public RouteController(RouteService routeService) {
		this.routeService = routeService;
	}

	@Operation(summary = "루트 생성", description = "선택한 컨텐츠와 여행 기간에 맞는 저장 루트를 반환합니다. MVP에서는 AI 호출 없이 DB 기반 추천 루트를 사용합니다.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "루트 생성 요청",
			required = true,
			content = @Content(schema = @Schema(implementation = RouteGenerateRequest.class))
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "루트 생성 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "CONTENT_NOT_FOUND 또는 ROUTE_NOT_FOUND",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@PostMapping("/generate")
	public ResponseEntity<ApiResponse<RouteResponse>> generateRoute(@RequestBody RouteGenerateRequest request) {
		// 루트 생성 처리
		RouteResponse response = routeService.generateRoute(request);
		return ResponseEntity.ok(ApiResponse.success(response, "루트 생성이 완료되었습니다."));
	}

	@Operation(summary = "루트 상세 조회", description = "루트 상세 정보와 지도에 표시할 장소 목록을 조회합니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "루트 상세 조회 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "ROUTE_NOT_FOUND: 존재하지 않거나 공개되지 않은 루트",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@GetMapping("/{routeId}")
	public ResponseEntity<ApiResponse<RouteResponse>> getRouteDetail(
			@Parameter(description = "루트 ID", example = "1")
			@PathVariable Long routeId
	) {
		// 루트 상세 조회
		RouteResponse response = routeService.getRouteDetail(routeId);
		return ResponseEntity.ok(ApiResponse.success(response, "루트 상세 조회가 완료되었습니다."));
	}

	@Operation(summary = "지역별 루트 조회", description = "특정 지역 ID에 속한 공개 추천 루트 목록을 조회합니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "지역별 루트 조회 성공",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	@GetMapping
	public ResponseEntity<ApiResponse<List<RouteResponse>>> getRoutesByRegion(
			@Parameter(description = "지역 ID", example = "3")
			@RequestParam Long regionId
	) {
		// 지역별 루트 조회
		List<RouteResponse> response = routeService.getRoutesByRegion(regionId);
		return ResponseEntity.ok(ApiResponse.success(response, "지역별 루트 조회가 완료되었습니다."));
	}
}
