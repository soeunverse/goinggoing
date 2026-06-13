package com.goinggoing.goinggoing.domain.auth.service;

import com.goinggoing.goinggoing.domain.auth.dto.AuthTokenResponse;
import com.goinggoing.goinggoing.domain.auth.dto.LoginRequest;
import com.goinggoing.goinggoing.domain.auth.dto.LogoutRequest;
import com.goinggoing.goinggoing.domain.auth.dto.RefreshTokenRequest;
import com.goinggoing.goinggoing.domain.auth.entity.RefreshToken;
import com.goinggoing.goinggoing.domain.auth.repository.RefreshTokenRepository;
import com.goinggoing.goinggoing.domain.user.entity.User;
import com.goinggoing.goinggoing.domain.user.entity.UserStatus;
import com.goinggoing.goinggoing.domain.user.repository.UserRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import com.goinggoing.goinggoing.global.security.AuthTokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthSessionServiceTest {

	private static final Clock FIXED_CLOCK = Clock.fixed(
			Instant.parse("2026-06-13T00:00:00Z"),
			ZoneId.of("Asia/Seoul")
	);

	private FakeUserRepository userRepository;
	private FakeRefreshTokenRepository refreshTokenRepository;
	private FixedAuthTokenGenerator tokenGenerator;
	private PasswordEncoder passwordEncoder;
	private AuthSessionService authSessionService;

	@BeforeEach
	void setUp() {
		userRepository = new FakeUserRepository();
		refreshTokenRepository = new FakeRefreshTokenRepository();
		tokenGenerator = new FixedAuthTokenGenerator();
		passwordEncoder = new BCryptPasswordEncoder();
		authSessionService = new AuthSessionService(
				userRepository,
				refreshTokenRepository,
				passwordEncoder,
				tokenGenerator,
				FIXED_CLOCK
		);
	}

	@Test
	@DisplayName("로그인 성공 시 access token과 refresh token을 발급한다")
	void loginSuccess() {
		User user = saveUser("user@example.com", "password123", UserStatus.ACTIVE);

		AuthTokenResponse response = authSessionService.login(new LoginRequest("user@example.com", "password123"));

		assertThat(response.userId()).isEqualTo(user.getId());
		assertThat(response.accessToken()).isEqualTo("access-1");
		assertThat(response.refreshToken()).isEqualTo("refresh-1");
		assertThat(response.tokenType()).isEqualTo("Bearer");
		assertThat(response.accessTokenExpiresInSeconds()).isEqualTo(3600L);
		assertThat(refreshTokenRepository.findByToken("refresh-1")).isPresent();
	}

	@Test
	@DisplayName("존재하지 않는 이메일로 로그인하면 INVALID_LOGIN_CREDENTIALS 예외가 발생한다")
	void unknownEmailLoginFails() {
		assertInvalidLogin(new LoginRequest("missing@example.com", "password123"));
	}

	@Test
	@DisplayName("비밀번호가 틀리면 INVALID_LOGIN_CREDENTIALS 예외가 발생한다")
	void wrongPasswordLoginFails() {
		saveUser("user@example.com", "password123", UserStatus.ACTIVE);

		assertInvalidLogin(new LoginRequest("user@example.com", "wrong1234"));
	}

	@Test
	@DisplayName("비활성 사용자는 로그인할 수 없다")
	void inactiveUserLoginFails() {
		saveUser("user@example.com", "password123", UserStatus.DELETED);

		assertThatThrownBy(() -> authSessionService.login(new LoginRequest("user@example.com", "password123")))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED));
	}

	@Test
	@DisplayName("토큰 갱신 성공 시 기존 refresh token을 폐기하고 새 토큰을 발급한다")
	void refreshSuccessRotatesRefreshToken() {
		User user = saveUser("user@example.com", "password123", UserStatus.ACTIVE);
		RefreshToken oldToken = refreshTokenRepository.save(RefreshToken.issue(
				user,
				"refresh-old",
				LocalDateTime.now(FIXED_CLOCK).plusDays(14)
		));

		AuthTokenResponse response = authSessionService.refresh(new RefreshTokenRequest("refresh-old"));

		assertThat(response.accessToken()).isEqualTo("access-1");
		assertThat(response.refreshToken()).isEqualTo("refresh-1");
		assertThat(oldToken.isRevoked()).isTrue();
		assertThat(refreshTokenRepository.findByToken("refresh-1")).isPresent();
	}

	@Test
	@DisplayName("폐기된 refresh token으로 갱신하면 INVALID_REFRESH_TOKEN 예외가 발생한다")
	void revokedRefreshTokenFails() {
		User user = saveUser("user@example.com", "password123", UserStatus.ACTIVE);
		RefreshToken token = refreshTokenRepository.save(RefreshToken.issue(
				user,
				"refresh-old",
				LocalDateTime.now(FIXED_CLOCK).plusDays(14)
		));
		token.revoke(LocalDateTime.now(FIXED_CLOCK));

		assertInvalidRefresh(new RefreshTokenRequest("refresh-old"));
	}

	@Test
	@DisplayName("만료된 refresh token으로 갱신하면 INVALID_REFRESH_TOKEN 예외가 발생한다")
	void expiredRefreshTokenFails() {
		User user = saveUser("user@example.com", "password123", UserStatus.ACTIVE);
		refreshTokenRepository.save(RefreshToken.issue(
				user,
				"refresh-old",
				LocalDateTime.now(FIXED_CLOCK).minusSeconds(1)
		));

		assertInvalidRefresh(new RefreshTokenRequest("refresh-old"));
	}

	@Test
	@DisplayName("로그아웃 성공 시 refresh token을 폐기한다")
	void logoutSuccessRevokesRefreshToken() {
		User user = saveUser("user@example.com", "password123", UserStatus.ACTIVE);
		RefreshToken token = refreshTokenRepository.save(RefreshToken.issue(
				user,
				"refresh-old",
				LocalDateTime.now(FIXED_CLOCK).plusDays(14)
		));

		authSessionService.logout(new LogoutRequest("refresh-old"));

		assertThat(token.isRevoked()).isTrue();
	}

	@Test
	@DisplayName("존재하지 않는 refresh token으로 로그아웃하면 INVALID_REFRESH_TOKEN 예외가 발생한다")
	void unknownRefreshTokenLogoutFails() {
		assertThatThrownBy(() -> authSessionService.logout(new LogoutRequest("missing-refresh")))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN));
	}

	private User saveUser(String email, String rawPassword, UserStatus status) {
		return userRepository.save(User.create(email, passwordEncoder.encode(rawPassword), "즉흥여행자")
				.withStatus(status));
	}

	private void assertInvalidLogin(LoginRequest request) {
		assertThatThrownBy(() -> authSessionService.login(request))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_LOGIN_CREDENTIALS));
	}

	private void assertInvalidRefresh(RefreshTokenRequest request) {
		assertThatThrownBy(() -> authSessionService.refresh(request))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN));
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
		public User save(User user) {
			User savedUser = user.hasId() ? user : user.withId(sequence++);
			users.put(savedUser.getEmail(), savedUser);
			return savedUser;
		}
	}

	private static class FakeRefreshTokenRepository implements RefreshTokenRepository {

		private final Map<String, RefreshToken> refreshTokens = new HashMap<>();
		private long sequence = 1L;

		@Override
		public Optional<RefreshToken> findByToken(String token) {
			return Optional.ofNullable(refreshTokens.get(token));
		}

		@Override
		public RefreshToken save(RefreshToken refreshToken) {
			RefreshToken savedToken = refreshToken.hasId() ? refreshToken : refreshToken.withId(sequence++);
			refreshTokens.put(savedToken.getToken(), savedToken);
			return savedToken;
		}
	}

	private static class FixedAuthTokenGenerator implements AuthTokenGenerator {

		private int sequence = 1;

		@Override
		public String generateAccessToken() {
			return "access-" + sequence;
		}

		@Override
		public String generateRefreshToken() {
			return "refresh-" + sequence++;
		}
	}
}
