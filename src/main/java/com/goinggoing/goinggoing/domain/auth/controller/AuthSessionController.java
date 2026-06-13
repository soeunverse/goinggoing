package com.goinggoing.goinggoing.domain.auth.controller;

import com.goinggoing.goinggoing.domain.auth.dto.AuthTokenResponse;
import com.goinggoing.goinggoing.domain.auth.dto.LoginRequest;
import com.goinggoing.goinggoing.domain.auth.dto.LogoutRequest;
import com.goinggoing.goinggoing.domain.auth.dto.RefreshTokenRequest;
import com.goinggoing.goinggoing.domain.auth.service.AuthSessionService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증", description = "회원가입, 로그인, 토큰 관리 API")
public class AuthSessionController {

	private final AuthSessionService authSessionService;

	public AuthSessionController(AuthSessionService authSessionService) {
		this.authSessionService = authSessionService;
	}

	@PostMapping("/login")
	@Operation(summary = "로그인", description = "이메일과 비밀번호를 검증하고 access token, refresh token을 발급합니다.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "로그인 요청 정보",
			required = true,
			content = @Content(schema = @Schema(implementation = LoginRequest.class))
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "로그인 성공",
					content = @Content(
							schema = @Schema(implementation = ApiResponse.class),
							examples = @ExampleObject(value = """
									{
									  "success": true,
									  "data": {
									    "userId": 1,
									    "accessToken": "access-token",
									    "refreshToken": "refresh-token",
									    "tokenType": "Bearer",
									    "accessTokenExpiresInSeconds": 3600
									  },
									  "message": "로그인이 완료되었습니다.",
									  "errorCode": null
									}
									""")
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "401",
					description = "로그인 실패. INVALID_LOGIN_CREDENTIALS, UNAUTHORIZED",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "서버 내부 오류. INTERNAL_SERVER_ERROR",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	public ResponseEntity<ApiResponse<AuthTokenResponse>> login(@RequestBody LoginRequest request) {
		// 로그인 검증 및 토큰 발급
		AuthTokenResponse response = authSessionService.login(request);
		// 로그인 성공 응답 반환
		return ResponseEntity.ok(ApiResponse.success(response, "로그인이 완료되었습니다."));
	}

	@PostMapping("/refresh")
	@Operation(summary = "토큰 갱신", description = "refresh token을 검증하고 기존 refresh token을 폐기한 뒤 새 토큰 쌍을 발급합니다.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "토큰 갱신 요청 정보",
			required = true,
			content = @Content(schema = @Schema(implementation = RefreshTokenRequest.class))
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "토큰 갱신 성공",
					content = @Content(
							schema = @Schema(implementation = ApiResponse.class),
							examples = @ExampleObject(value = """
									{
									  "success": true,
									  "data": {
									    "userId": 1,
									    "accessToken": "new-access-token",
									    "refreshToken": "new-refresh-token",
									    "tokenType": "Bearer",
									    "accessTokenExpiresInSeconds": 3600
									  },
									  "message": "토큰이 갱신되었습니다.",
									  "errorCode": null
									}
									""")
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "401",
					description = "유효하지 않은 refresh token. INVALID_REFRESH_TOKEN",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "서버 내부 오류. INTERNAL_SERVER_ERROR",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	public ResponseEntity<ApiResponse<AuthTokenResponse>> refresh(@RequestBody RefreshTokenRequest request) {
		// refresh token 검증 및 재발급
		AuthTokenResponse response = authSessionService.refresh(request);
		// 토큰 갱신 응답 반환
		return ResponseEntity.ok(ApiResponse.success(response, "토큰이 갱신되었습니다."));
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃", description = "refresh token을 검증하고 폐기합니다.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "로그아웃 요청 정보",
			required = true,
			content = @Content(schema = @Schema(implementation = LogoutRequest.class))
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "로그아웃 성공",
					content = @Content(
							schema = @Schema(implementation = ApiResponse.class),
							examples = @ExampleObject(value = """
									{
									  "success": true,
									  "data": null,
									  "message": "로그아웃이 완료되었습니다.",
									  "errorCode": null
									}
									""")
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "401",
					description = "유효하지 않은 refresh token. INVALID_REFRESH_TOKEN",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "서버 내부 오류. INTERNAL_SERVER_ERROR",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequest request) {
		// refresh token 폐기
		authSessionService.logout(request);
		// 로그아웃 성공 응답 반환
		return ResponseEntity.ok(ApiResponse.success(null, "로그아웃이 완료되었습니다."));
	}
}
