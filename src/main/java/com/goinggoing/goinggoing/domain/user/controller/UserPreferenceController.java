package com.goinggoing.goinggoing.domain.user.controller;

import com.goinggoing.goinggoing.domain.user.dto.UserPreferenceRequest;
import com.goinggoing.goinggoing.domain.user.dto.UserPreferenceResponse;
import com.goinggoing.goinggoing.domain.user.service.UserPreferenceService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me/preferences")
@Tag(name = "사용자", description = "사용자 내 정보 및 온보딩 취향 API")
public class UserPreferenceController {

	private final UserPreferenceService userPreferenceService;

	public UserPreferenceController(UserPreferenceService userPreferenceService) {
		this.userPreferenceService = userPreferenceService;
	}

	@GetMapping
	@Operation(summary = "온보딩 취향 조회", description = "로그인 사용자의 저장된 온보딩 취향을 조회합니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "온보딩 취향 조회 성공",
					content = @Content(
							schema = @Schema(implementation = ApiResponse.class),
							examples = @ExampleObject(value = """
									{
									  "success": true,
									  "data": {
									    "preferredTripDuration": "DAY_TRIP",
									    "regionIds": [1, 2],
									    "themeIds": [10],
									    "tagIds": [100, 101]
									  },
									  "message": "온보딩 취향 조회가 완료되었습니다.",
									  "errorCode": null
									}
									""")
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "401",
					description = "인증 헤더 누락 또는 비활성 사용자. UNAUTHORIZED",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "사용자 없음. USER_NOT_FOUND",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "서버 내부 오류. INTERNAL_SERVER_ERROR",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	public ResponseEntity<ApiResponse<UserPreferenceResponse>> getMyPreference(
			@Parameter(description = "로그인 사용자 ID", required = true, example = "1")
			@RequestHeader("X-USER-ID") Long userId
	) {
		// 온보딩 취향 조회 처리
		UserPreferenceResponse response = userPreferenceService.getMyPreference(userId);
		// 취향 조회 응답 반환
		return ResponseEntity.ok(ApiResponse.success(response, "온보딩 취향 조회가 완료되었습니다."));
	}

	@PutMapping
	@Operation(summary = "온보딩 취향 저장", description = "로그인 사용자의 온보딩 취향을 생성하거나 기존 취향을 교체합니다.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "온보딩 취향 저장 요청 정보",
			required = true,
			content = @Content(schema = @Schema(implementation = UserPreferenceRequest.class))
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "온보딩 취향 저장 성공",
					content = @Content(
							schema = @Schema(implementation = ApiResponse.class),
							examples = @ExampleObject(value = """
									{
									  "success": true,
									  "data": {
									    "preferredTripDuration": "ONE_NIGHT_TWO_DAYS",
									    "regionIds": [3],
									    "themeIds": [11, 12],
									    "tagIds": []
									  },
									  "message": "온보딩 취향 저장이 완료되었습니다.",
									  "errorCode": null
									}
									""")
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "온보딩 취향 요청 오류. INVALID_PREFERENCE",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "401",
					description = "인증 헤더 누락 또는 비활성 사용자. UNAUTHORIZED",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "사용자 없음. USER_NOT_FOUND",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "서버 내부 오류. INTERNAL_SERVER_ERROR",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	public ResponseEntity<ApiResponse<UserPreferenceResponse>> saveMyPreference(
			@Parameter(description = "로그인 사용자 ID", required = true, example = "1")
			@RequestHeader("X-USER-ID") Long userId,
			@RequestBody UserPreferenceRequest request
	) {
		// 온보딩 취향 저장 처리
		UserPreferenceResponse response = userPreferenceService.saveMyPreference(userId, request);
		// 취향 저장 응답 반환
		return ResponseEntity.ok(ApiResponse.success(response, "온보딩 취향 저장이 완료되었습니다."));
	}
}
