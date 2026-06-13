package com.goinggoing.goinggoing.domain.user.service;

import com.goinggoing.goinggoing.domain.user.dto.UserSignupRequest;
import com.goinggoing.goinggoing.domain.user.dto.UserSignupResponse;
import com.goinggoing.goinggoing.domain.user.entity.User;
import com.goinggoing.goinggoing.domain.user.repository.UserRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserSignupServiceTest {

	private FakeUserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private UserSignupService userSignupService;

	@BeforeEach
	void setUp() {
		userRepository = new FakeUserRepository();
		passwordEncoder = new BCryptPasswordEncoder();
		userSignupService = new UserSignupService(userRepository, passwordEncoder);
	}

	@Test
	@DisplayName("회원가입 성공 시 ACTIVE 사용자와 응답 정보를 생성한다")
	void signupSuccess() {
		UserSignupRequest request = new UserSignupRequest(
				"user@example.com",
				"password123",
				"즉흥여행자"
		);

		UserSignupResponse response = userSignupService.signup(request);

		assertThat(response.userId()).isNotNull();
		assertThat(response.email()).isEqualTo("user@example.com");
		assertThat(response.nickname()).isEqualTo("즉흥여행자");

		User savedUser = userRepository.findByEmail("user@example.com").orElseThrow();
		assertThat(savedUser.isActive()).isTrue();
	}

	@Test
	@DisplayName("회원가입 시 비밀번호는 평문이 아닌 암호화 값으로 저장한다")
	void signupEncodesPassword() {
		UserSignupRequest request = new UserSignupRequest(
				"user@example.com",
				"password123",
				"즉흥여행자"
		);

		userSignupService.signup(request);

		User savedUser = userRepository.findByEmail("user@example.com").orElseThrow();
		assertThat(savedUser.getPasswordHash()).isNotEqualTo("password123");
		assertThat(passwordEncoder.matches("password123", savedUser.getPasswordHash())).isTrue();
	}

	@Test
	@DisplayName("이미 가입된 이메일이면 EMAIL_ALREADY_EXISTS 예외가 발생한다")
	void duplicateEmailFails() {
		userRepository.save(User.create("user@example.com", "encoded-password", "기존사용자"));

		UserSignupRequest request = new UserSignupRequest(
				"user@example.com",
				"password123",
				"즉흥여행자"
		);

		assertThatThrownBy(() -> userSignupService.signup(request))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS));
	}

	@Test
	@DisplayName("비밀번호가 8자 미만이면 INVALID_PASSWORD 예외가 발생한다")
	void shortPasswordFails() {
		UserSignupRequest request = new UserSignupRequest(
				"user@example.com",
				"pass12",
				"즉흥여행자"
		);

		assertInvalidPassword(request);
	}

	@Test
	@DisplayName("비밀번호에 영문이 없으면 INVALID_PASSWORD 예외가 발생한다")
	void passwordWithoutAlphabetFails() {
		UserSignupRequest request = new UserSignupRequest(
				"user@example.com",
				"12345678",
				"즉흥여행자"
		);

		assertInvalidPassword(request);
	}

	@Test
	@DisplayName("비밀번호에 숫자가 없으면 INVALID_PASSWORD 예외가 발생한다")
	void passwordWithoutNumberFails() {
		UserSignupRequest request = new UserSignupRequest(
				"user@example.com",
				"password",
				"즉흥여행자"
		);

		assertInvalidPassword(request);
	}

	@Test
	@DisplayName("비밀번호가 100자를 초과하면 INVALID_PASSWORD 예외가 발생한다")
	void tooLongPasswordFails() {
		String password = "password123" + "a".repeat(100);
		UserSignupRequest request = new UserSignupRequest(
				"user@example.com",
				password,
				"즉흥여행자"
		);

		assertInvalidPassword(request);
	}

	@Test
	@DisplayName("닉네임이 공백이면 INVALID_NICKNAME 예외가 발생한다")
	void blankNicknameFails() {
		UserSignupRequest request = new UserSignupRequest(
				"user@example.com",
				"password123",
				" "
		);

		assertThatThrownBy(() -> userSignupService.signup(request))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_NICKNAME));
	}

	@Test
	@DisplayName("닉네임 중복은 허용한다")
	void duplicateNicknameAllowed() {
		userRepository.save(User.create("first@example.com", "encoded-password", "즉흥여행자"));
		UserSignupRequest request = new UserSignupRequest(
				"second@example.com",
				"password123",
				"즉흥여행자"
		);

		UserSignupResponse response = userSignupService.signup(request);

		assertThat(response.email()).isEqualTo("second@example.com");
		assertThat(userRepository.count()).isEqualTo(2);
	}

	private void assertInvalidPassword(UserSignupRequest request) {
		assertThatThrownBy(() -> userSignupService.signup(request))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD));
	}

	private static class FakeUserRepository implements UserRepository {

		private final Map<String, User> users = new HashMap<>();
		private long sequence = 1L;

		@Override
		public boolean existsByEmail(String email) {
			return users.containsKey(email);
		}

		@Override
		public Optional<User> findByEmail(String email) {
			return Optional.ofNullable(users.get(email));
		}

		@Override
		public Optional<User> findById(Long id) {
			return users.values().stream()
					.filter(user -> user.getId().equals(id))
					.findFirst();
		}

		@Override
		public User save(User user) {
			User savedUser = user.hasId() ? user : user.withId(sequence++);
			users.put(savedUser.getEmail(), savedUser);
			return savedUser;
		}

		long count() {
			return users.size();
		}
	}
}
