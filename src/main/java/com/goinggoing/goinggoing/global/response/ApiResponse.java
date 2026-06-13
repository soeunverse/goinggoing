package com.goinggoing.goinggoing.global.response;

import com.goinggoing.goinggoing.global.exception.ErrorCode;

public record ApiResponse<T>(
		boolean success,
		T data,
		String message,
		String errorCode
) {

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, data, null, null);
	}

	public static <T> ApiResponse<T> success(T data, String message) {
		return new ApiResponse<>(true, data, message, null);
	}

	public static ApiResponse<Void> successWithoutData() {
		return new ApiResponse<>(true, null, null, null);
	}

	public static ApiResponse<Void> failure(ErrorCode errorCode) {
		return failure(errorCode, errorCode.getMessage());
	}

	public static ApiResponse<Void> failure(ErrorCode errorCode, String message) {
		return new ApiResponse<>(false, null, message, errorCode.name());
	}
}
