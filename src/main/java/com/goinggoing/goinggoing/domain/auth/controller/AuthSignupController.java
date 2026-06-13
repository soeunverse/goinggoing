package com.goinggoing.goinggoing.domain.auth.controller;

import com.goinggoing.goinggoing.domain.user.dto.UserSignupRequest;
import com.goinggoing.goinggoing.domain.user.dto.UserSignupResponse;
import com.goinggoing.goinggoing.domain.user.service.UserSignupService;
import com.goinggoing.goinggoing.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증", description = "회원가입, 로그인, 토큰 관리 API")
public class AuthSignupController {

	private final UserSignupService userSignupService;

	public AuthSignupController(UserSignupService userSignupService) {
		this.userSignupService = userSignupService;
	}

	@PostMapping("/signup")
	@Operation(
			summary = "회원가입",
			description = "이메일, 비밀번호, 닉네임으로 신규 사용자를 생성합니다."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "회원가입 요청 정보",
			required = true,
			content = @Content(schema = @Schema(implementation = UserSignupRequest.class))
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "201",
					description = "회원가입 성공",
					content = @Content(
							schema = @Schema(implementation = ApiResponse.class),
							examples = @ExampleObject(value = """
									{
									  "success": true,
									  "data": {
									    "userId": 1,
									    "email": "user@example.com",
									    "nickname": "즉흥여행자"
									  },
									  "message": "회원가입이 완료되었습니다.",
									  "errorCode": null
									}
									""")
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "요청 값 검증 실패. INVALID_PASSWORD, INVALID_NICKNAME, BAD_REQUEST",
					content = @Content(
							schema = @Schema(implementation = ApiResponse.class),
							examples = @ExampleObject(value = """
									{
									  "success": false,
									  "data": null,
									  "message": "비밀번호는 8자 이상 100자 이하이며 영문과 숫자를 포함해야 합니다.",
									  "errorCode": "INVALID_PASSWORD"
									}
									""")
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "409",
					description = "이미 가입된 이메일. EMAIL_ALREADY_EXISTS",
					content = @Content(
							schema = @Schema(implementation = ApiResponse.class),
							examples = @ExampleObject(value = """
									{
									  "success": false,
									  "data": null,
									  "message": "이미 가입된 이메일입니다.",
									  "errorCode": "EMAIL_ALREADY_EXISTS"
									}
									""")
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "서버 내부 오류. INTERNAL_SERVER_ERROR",
					content = @Content(schema = @Schema(implementation = ApiResponse.class))
			)
	})
	public ResponseEntity<ApiResponse<UserSignupResponse>> signup(@RequestBody UserSignupRequest request) {
		// 회원가입 정보 검증 및 저장
		UserSignupResponse response = userSignupService.signup(request);
		// 생성 완료 응답 반환
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(ApiResponse.success(response, "회원가입이 완료되었습니다."));
	}
}
