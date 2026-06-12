package com.goinggoing.goinggoing.global.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionTest {

	@Test
	@DisplayName("BusinessException은 ErrorCode와 기본 메시지를 보관한다")
	void keepsErrorCodeAndDefaultMessage() {
		BusinessException exception = new BusinessException(ErrorCode.UNAUTHORIZED);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
		assertThat(exception.getMessage()).isEqualTo("인증이 필요합니다.");
	}

	@Test
	@DisplayName("BusinessException은 커스텀 메시지를 보관한다")
	void keepsCustomMessage() {
		BusinessException exception = new BusinessException(ErrorCode.BAD_REQUEST, "필수값이 누락되었습니다.");

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BAD_REQUEST);
		assertThat(exception.getMessage()).isEqualTo("필수값이 누락되었습니다.");
	}
}
