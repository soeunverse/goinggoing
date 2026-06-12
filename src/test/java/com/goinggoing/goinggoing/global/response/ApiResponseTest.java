package com.goinggoing.goinggoing.global.response;

import com.goinggoing.goinggoing.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

	@Test
	@DisplayName("success(data)는 성공 상태와 데이터를 반환한다")
	void successWithData() {
		String data = "goinggoing";

		ApiResponse<String> response = ApiResponse.success(data);

		assertThat(response.success()).isTrue();
		assertThat(response.data()).isEqualTo(data);
		assertThat(response.message()).isNull();
		assertThat(response.errorCode()).isNull();
	}

	@Test
	@DisplayName("success(data, message)는 성공 상태, 데이터, 메시지를 반환한다")
	void successWithDataAndMessage() {
		ApiResponse<String> response = ApiResponse.success("created", "생성되었습니다.");

		assertThat(response.success()).isTrue();
		assertThat(response.data()).isEqualTo("created");
		assertThat(response.message()).isEqualTo("생성되었습니다.");
		assertThat(response.errorCode()).isNull();
	}

	@Test
	@DisplayName("successWithoutData()는 data 없이 성공 상태를 반환한다")
	void successWithoutData() {
		ApiResponse<Void> response = ApiResponse.successWithoutData();

		assertThat(response.success()).isTrue();
		assertThat(response.data()).isNull();
		assertThat(response.message()).isNull();
		assertThat(response.errorCode()).isNull();
	}

	@Test
	@DisplayName("failure(errorCode)는 실패 상태와 기본 메시지를 반환한다")
	void failureWithErrorCode() {
		ApiResponse<Void> response = ApiResponse.failure(ErrorCode.CONTENT_NOT_FOUND);

		assertThat(response.success()).isFalse();
		assertThat(response.data()).isNull();
		assertThat(response.message()).isEqualTo("컨텐츠를 찾을 수 없습니다.");
		assertThat(response.errorCode()).isEqualTo("CONTENT_NOT_FOUND");
	}

	@Test
	@DisplayName("failure(errorCode, message)는 기본 메시지를 대체한다")
	void failureWithCustomMessage() {
		ApiResponse<Void> response = ApiResponse.failure(ErrorCode.BAD_REQUEST, "요청값을 확인해주세요.");

		assertThat(response.success()).isFalse();
		assertThat(response.data()).isNull();
		assertThat(response.message()).isEqualTo("요청값을 확인해주세요.");
		assertThat(response.errorCode()).isEqualTo("BAD_REQUEST");
	}
}
