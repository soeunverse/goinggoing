package com.goinggoing.goinggoing.global.exception;

import com.goinggoing.goinggoing.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
		// 비즈니스 예외를 정의된 상태 코드로 변환
		ErrorCode errorCode = exception.getErrorCode();
		return ResponseEntity
				.status(errorCode.getStatus())
				.body(ApiResponse.failure(errorCode, exception.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
		// 예상하지 못한 예외를 서버 에러 응답으로 변환
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		return ResponseEntity
				.status(errorCode.getStatus())
				.body(ApiResponse.failure(errorCode));
	}
}
