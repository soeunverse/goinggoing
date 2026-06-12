package com.goinggoing.goinggoing.global.exception;

import com.goinggoing.goinggoing.global.response.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

	private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

	@Test
	@DisplayName("BusinessException은 ErrorCode에 맞는 HTTP 상태와 실패 응답으로 변환된다")
	void handleBusinessException() {
		BusinessException exception = new BusinessException(ErrorCode.CONTENT_NOT_FOUND);

		ResponseEntity<ApiResponse<Void>> response = handler.handleBusinessException(exception);

		assertThat(response.getStatusCode()).isEqualTo(ErrorCode.CONTENT_NOT_FOUND.getStatus());
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().success()).isFalse();
		assertThat(response.getBody().errorCode()).isEqualTo("CONTENT_NOT_FOUND");
		assertThat(response.getBody().message()).isEqualTo("컨텐츠를 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("예상하지 못한 예외는 INTERNAL_SERVER_ERROR 응답으로 변환된다")
	void handleUnexpectedException() {
		ResponseEntity<ApiResponse<Void>> response = handler.handleException(new RuntimeException("boom"));

		assertThat(response.getStatusCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.getStatus());
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().success()).isFalse();
		assertThat(response.getBody().errorCode()).isEqualTo("INTERNAL_SERVER_ERROR");
		assertThat(response.getBody().message()).isEqualTo("서버 오류가 발생했습니다.");
	}
}
