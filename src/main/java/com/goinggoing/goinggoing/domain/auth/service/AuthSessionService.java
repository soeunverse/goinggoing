package com.goinggoing.goinggoing.domain.auth.service;

import com.goinggoing.goinggoing.domain.auth.dto.AuthTokenResponse;
import com.goinggoing.goinggoing.domain.auth.dto.LoginRequest;
import com.goinggoing.goinggoing.domain.auth.dto.LogoutRequest;
import com.goinggoing.goinggoing.domain.auth.dto.RefreshTokenRequest;
import com.goinggoing.goinggoing.domain.auth.entity.RefreshToken;
import com.goinggoing.goinggoing.domain.auth.repository.RefreshTokenRepository;
import com.goinggoing.goinggoing.domain.user.entity.User;
import com.goinggoing.goinggoing.domain.user.repository.UserRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import com.goinggoing.goinggoing.global.security.AuthTokenGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@Transactional
public class AuthSessionService {

	private static final String TOKEN_TYPE = "Bearer";
	private static final long ACCESS_TOKEN_EXPIRES_IN_SECONDS = 3600L;
	private static final long REFRESH_TOKEN_EXPIRES_IN_DAYS = 14L;

	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthTokenGenerator authTokenGenerator;
	private final Clock clock;

	public AuthSessionService(
			UserRepository userRepository,
			RefreshTokenRepository refreshTokenRepository,
			PasswordEncoder passwordEncoder,
			AuthTokenGenerator authTokenGenerator,
			Clock clock
	) {
		this.userRepository = userRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.authTokenGenerator = authTokenGenerator;
		this.clock = clock;
	}

	public AuthTokenResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.email())
				.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS));

		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS);
		}

		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.UNAUTHORIZED);
		}

		return issueTokenPair(user);
	}

	public AuthTokenResponse refresh(RefreshTokenRequest request) {
		RefreshToken refreshToken = findAvailableRefreshToken(request.refreshToken());
		refreshToken.revoke(now());

		return issueTokenPair(refreshToken.getUser());
	}

	public void logout(LogoutRequest request) {
		RefreshToken refreshToken = findAvailableRefreshToken(request.refreshToken());
		refreshToken.revoke(now());
	}

	private AuthTokenResponse issueTokenPair(User user) {
		String accessToken = authTokenGenerator.generateAccessToken();
		String refreshToken = authTokenGenerator.generateRefreshToken();
		refreshTokenRepository.save(RefreshToken.issue(user, refreshToken, now().plusDays(REFRESH_TOKEN_EXPIRES_IN_DAYS)));

		return new AuthTokenResponse(
				user.getId(),
				accessToken,
				refreshToken,
				TOKEN_TYPE,
				ACCESS_TOKEN_EXPIRES_IN_SECONDS
		);
	}

	private RefreshToken findAvailableRefreshToken(String token) {
		RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
				.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

		if (!refreshToken.isAvailable(now())) {
			throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		return refreshToken;
	}

	private LocalDateTime now() {
		return LocalDateTime.now(clock);
	}
}
