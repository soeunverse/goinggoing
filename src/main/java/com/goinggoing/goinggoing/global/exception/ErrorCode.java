package com.goinggoing.goinggoing.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
	EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
	INVALID_LOGIN_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 refresh token입니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호 형식이 올바르지 않습니다."),
	INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "닉네임 형식이 올바르지 않습니다."),
	INVALID_PREFERENCE(HttpStatus.BAD_REQUEST, "온보딩 취향 정보가 올바르지 않습니다."),
	CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
	CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "컨텐츠를 찾을 수 없습니다."),
	BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "찜 항목을 찾을 수 없습니다."),
	BOOKMARK_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 찜한 컨텐츠입니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

	private final HttpStatus status;
	private final String message;

	ErrorCode(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}
