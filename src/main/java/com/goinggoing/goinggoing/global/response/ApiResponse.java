package com.goinggoing.goinggoing.global.response;

import com.goinggoing.goinggoing.global.exception.ErrorCode;

public record ApiResponse<T>(
		boolean success,
		T data,
		String message,
		String errorCode
) {

	public static <T> ApiResponse<T> success(T data) {
		// 데이터 포함 성공 응답 생성
		return new ApiResponse<>(true, data, null, null);
	}

	public static <T> ApiResponse<T> success(T data, String message) {
		// 메시지 포함 성공 응답 생성
		return new ApiResponse<>(true, data, message, null);
	}

	public static ApiResponse<Void> successWithoutData() {
		// 본문 없는 성공 응답 생성
		return new ApiResponse<>(true, null, null, null);
	}

	public static ApiResponse<Void> failure(ErrorCode errorCode) {
		// 기본 에러 메시지로 실패 응답 생성
		return failure(errorCode, errorCode.getMessage());
	}

	public static ApiResponse<Void> failure(ErrorCode errorCode, String message) {
		// 커스텀 에러 메시지로 실패 응답 생성
		return new ApiResponse<>(false, null, message, errorCode.name());
	}
}
