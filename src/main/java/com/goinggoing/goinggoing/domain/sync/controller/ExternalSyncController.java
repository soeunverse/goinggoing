package com.goinggoing.goinggoing.domain.sync.controller;

import com.goinggoing.goinggoing.domain.sync.dto.ExternalSyncLogResponse;
import com.goinggoing.goinggoing.domain.sync.dto.ExternalSyncResponse;
import com.goinggoing.goinggoing.domain.sync.service.ExternalSyncService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import com.goinggoing.goinggoing.global.security.CurrentUserExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sync")
@Tag(name = "외부 데이터 동기화", description = "관리자 전용 한국관광공사 공공데이터 동기화 API")
public class ExternalSyncController {

	private final ExternalSyncService externalSyncService;
	private final CurrentUserExtractor currentUserExtractor;

	public ExternalSyncController(ExternalSyncService externalSyncService, CurrentUserExtractor currentUserExtractor) {
		this.externalSyncService = externalSyncService;
		this.currentUserExtractor = currentUserExtractor;
	}

	@Operation(summary = "컨텐츠 동기화", description = "한국관광공사 컨텐츠 데이터를 동기화합니다. 현재는 실제 HTTP 호출 전 client 인터페이스 기반 준비 단계입니다.")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "컨텐츠 동기화 실행 완료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "UNAUTHORIZED: 인증 헤더 누락 또는 비활성 사용자", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "FORBIDDEN: 관리자 권한 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
	})
	@PostMapping("/contents")
	public ResponseEntity<ApiResponse<ExternalSyncResponse>> syncContents(
			@Parameter(hidden = true)
			@RequestHeader("Authorization") String authorizationHeader
	) {
		// 관리자 인증 처리
		Long adminUserId = currentUserExtractor.extractUserId(authorizationHeader);
		// 컨텐츠 동기화 실행
		ExternalSyncResponse response = externalSyncService.syncContents(adminUserId);
		return ResponseEntity.ok(ApiResponse.success(response, "컨텐츠 동기화가 완료되었습니다."));
	}

	@Operation(summary = "연관 관광지 동기화", description = "한국관광공사 관광지별 연관 관광지 데이터를 동기화합니다. 현재는 client 인터페이스 기반 준비 단계입니다.")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "연관 관광지 동기화 실행 완료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "UNAUTHORIZED: 인증 헤더 누락 또는 비활성 사용자", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "FORBIDDEN: 관리자 권한 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
	})
	@PostMapping("/related-places")
	public ResponseEntity<ApiResponse<ExternalSyncResponse>> syncRelatedPlaces(
			@Parameter(hidden = true)
			@RequestHeader("Authorization") String authorizationHeader
	) {
		// 관리자 인증 처리
		Long adminUserId = currentUserExtractor.extractUserId(authorizationHeader);
		// 연관 관광지 동기화 실행
		ExternalSyncResponse response = externalSyncService.syncRelatedPlaces(adminUserId);
		return ResponseEntity.ok(ApiResponse.success(response, "연관 관광지 동기화가 완료되었습니다."));
	}

	@Operation(summary = "지역수요 동기화", description = "한국관광공사 지역별 관광 자원 수요 데이터를 동기화합니다. 현재는 client 인터페이스 기반 준비 단계입니다.")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "지역수요 동기화 실행 완료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "UNAUTHORIZED: 인증 헤더 누락 또는 비활성 사용자", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "FORBIDDEN: 관리자 권한 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
	})
	@PostMapping("/regional-demand")
	public ResponseEntity<ApiResponse<ExternalSyncResponse>> syncRegionalDemand(
			@Parameter(hidden = true)
			@RequestHeader("Authorization") String authorizationHeader
	) {
		// 관리자 인증 처리
		Long adminUserId = currentUserExtractor.extractUserId(authorizationHeader);
		// 지역수요 동기화 실행
		ExternalSyncResponse response = externalSyncService.syncRegionalDemand(adminUserId);
		return ResponseEntity.ok(ApiResponse.success(response, "지역수요 동기화가 완료되었습니다."));
	}

	@Operation(summary = "동기화 로그 조회", description = "외부 데이터 동기화 실행 결과 로그를 최신순으로 조회합니다.")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "동기화 로그 조회 성공", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "UNAUTHORIZED: 인증 헤더 누락 또는 비활성 사용자", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "FORBIDDEN: 관리자 권한 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
	})
	@GetMapping("/logs")
	public ResponseEntity<ApiResponse<List<ExternalSyncLogResponse>>> getSyncLogs(
			@Parameter(hidden = true)
			@RequestHeader("Authorization") String authorizationHeader
	) {
		// 관리자 인증 처리
		Long adminUserId = currentUserExtractor.extractUserId(authorizationHeader);
		// 동기화 로그 조회
		List<ExternalSyncLogResponse> response = externalSyncService.getSyncLogs(adminUserId);
		return ResponseEntity.ok(ApiResponse.success(response, "동기화 로그 조회가 완료되었습니다."));
	}
}
