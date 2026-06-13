package com.goinggoing.goinggoing.domain.route.controller;

import com.goinggoing.goinggoing.domain.route.dto.RouteGenerateRequest;
import com.goinggoing.goinggoing.domain.route.dto.RouteResponse;
import com.goinggoing.goinggoing.domain.route.service.RouteService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
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
public class RouteController {

	private final RouteService routeService;

	public RouteController(RouteService routeService) {
		this.routeService = routeService;
	}

	@PostMapping("/generate")
	public ResponseEntity<ApiResponse<RouteResponse>> generateRoute(@RequestBody RouteGenerateRequest request) {
		RouteResponse response = routeService.generateRoute(request);
		return ResponseEntity.ok(ApiResponse.success(response, "루트 생성이 완료되었습니다."));
	}

	@GetMapping("/{routeId}")
	public ResponseEntity<ApiResponse<RouteResponse>> getRouteDetail(@PathVariable Long routeId) {
		RouteResponse response = routeService.getRouteDetail(routeId);
		return ResponseEntity.ok(ApiResponse.success(response, "루트 상세 조회가 완료되었습니다."));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<RouteResponse>>> getRoutesByRegion(@RequestParam Long regionId) {
		List<RouteResponse> response = routeService.getRoutesByRegion(regionId);
		return ResponseEntity.ok(ApiResponse.success(response, "지역별 루트 조회가 완료되었습니다."));
	}
}
