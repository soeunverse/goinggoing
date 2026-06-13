package com.goinggoing.goinggoing.domain.user.controller;

import com.goinggoing.goinggoing.domain.user.dto.UserProfileResponse;
import com.goinggoing.goinggoing.domain.user.dto.UserProfileUpdateRequest;
import com.goinggoing.goinggoing.domain.user.service.UserProfileService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "사용자", description = "사용자 내 정보 API")
public class UserProfileController {

	private final UserProfileService userProfileService;

	public UserProfileController(UserProfileService userProfileService) {
		this.userProfileService = userProfileService;
	}

	@GetMapping("/me")
	@Operation(summary = "내 정보 조회", description = "로그인 사용자의 이메일, 닉네임, 상태 정보를 조회합니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "내 정보 조회 성공",
					content = @Content(
							schema = @Schema(implementation = ApiResponse.class),
							examples = @ExampleObject(value = """
									{
									  "success": true,
									  "data": {
									    "userId": 1,
									    "email": "user@example.com",
									    "nickname": "즉흥여행자",
									    "status": "ACTIVE"
									  },
									  "message": "내 정보 조회가 완료되었습니다.",
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
	public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
			@Parameter(description = "로그인 사용자 ID", required = true, example = "1")
			@RequestHeader("X-USER-ID") Long userId
	) {
		// 내 정보 조회 처리
		UserProfileResponse response = userProfileService.getMyProfile(userId);
		// 내 정보 응답 반환
		return ResponseEntity.ok(ApiResponse.success(response, "내 정보 조회가 완료되었습니다."));
	}

	@PatchMapping("/me")
	public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(
			@RequestHeader("X-USER-ID") Long userId,
			@RequestBody UserProfileUpdateRequest request
	) {
		UserProfileResponse response = userProfileService.updateMyProfile(userId, request);
		return ResponseEntity.ok(ApiResponse.success(response, "내 정보 수정이 완료되었습니다."));
	}

	@DeleteMapping("/me")
	public ResponseEntity<ApiResponse<Void>> withdraw(@RequestHeader("X-USER-ID") Long userId) {
		userProfileService.withdraw(userId);
		return ResponseEntity.ok(ApiResponse.success(null, "회원 탈퇴가 완료되었습니다."));
	}
}
