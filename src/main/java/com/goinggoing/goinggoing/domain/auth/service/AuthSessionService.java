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
import com.goinggoing.goinggoing.global.security.AuthTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class AuthSessionService {

	private static final String TOKEN_TYPE = "Bearer";
	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthTokenProvider authTokenProvider;
	private final Clock clock;
	private final long accessTokenExpiresInSeconds;
	private final long refreshTokenExpiresInDays;

	public AuthSessionService(
			UserRepository userRepository,
			RefreshTokenRepository refreshTokenRepository,
			PasswordEncoder passwordEncoder,
			AuthTokenProvider authTokenProvider,
			Clock clock,
			@Value("${auth.token.access-token-expires-in-seconds:3600}") long accessTokenExpiresInSeconds,
			@Value("${auth.token.refresh-token-expires-in-days:14}") long refreshTokenExpiresInDays
	) {
		this.userRepository = userRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.authTokenProvider = authTokenProvider;
		this.clock = clock;
		this.accessTokenExpiresInSeconds = accessTokenExpiresInSeconds;
		this.refreshTokenExpiresInDays = refreshTokenExpiresInDays;
	}

	public AuthTokenResponse login(LoginRequest request) {
		// 이메일로 사용자 조회
		User user = userRepository.findByEmail(request.email())
				.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS));

		// 비밀번호 검증
		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS);
		}

		// 활성 계정 검증
		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.UNAUTHORIZED);
		}

		AuthTokenResponse response = issueTokenPair(user);
		log.info("[DB 저장] 로그인 토큰 발급 userId={} email={}", user.getId(), user.getEmail());
		return response;
	}

	public AuthTokenResponse refresh(RefreshTokenRequest request) {
		// refresh token 유효성 검증
		RefreshToken refreshToken = findAvailableRefreshToken(request.refreshToken());
		// 기존 refresh token 폐기
		refreshToken.revoke(now());
		log.info("[DB 수정] refresh token 폐기 userId={} refreshTokenId={}", refreshToken.getUser().getId(), refreshToken.getId());

		AuthTokenResponse response = issueTokenPair(refreshToken.getUser());
		log.info("[DB 저장] 토큰 갱신 완료 userId={}", refreshToken.getUser().getId());
		return response;
	}

	public void logout(LogoutRequest request) {
		// 로그아웃 대상 refresh token 검증
		RefreshToken refreshToken = findAvailableRefreshToken(request.refreshToken());
		// refresh token 폐기 처리
		refreshToken.revoke(now());
		log.info("[DB 수정] 로그아웃 refresh token 폐기 userId={} refreshTokenId={}", refreshToken.getUser().getId(), refreshToken.getId());
	}

	private AuthTokenResponse issueTokenPair(User user) {
		// access token 및 refresh token 생성
		String accessToken = authTokenProvider.generateAccessToken(user.getId(), now().plusSeconds(accessTokenExpiresInSeconds));
		String refreshToken = authTokenProvider.generateRefreshToken();
		// refresh token 저장
		RefreshToken savedRefreshToken = refreshTokenRepository.save(RefreshToken.issue(user, refreshToken, now().plusDays(refreshTokenExpiresInDays)));
		log.info("[DB 저장] refresh token 저장 userId={} refreshTokenId={} expiresInDays={}", user.getId(), savedRefreshToken.getId(), refreshTokenExpiresInDays);

		return new AuthTokenResponse(
				user.getId(),
				accessToken,
				refreshToken,
				TOKEN_TYPE,
				accessTokenExpiresInSeconds
		);
	}

	private RefreshToken findAvailableRefreshToken(String token) {
		// refresh token 조회
		RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
				.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

		// 만료 및 폐기 여부 검증
		if (!refreshToken.isAvailable(now())) {
			throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		return refreshToken;
	}

	private LocalDateTime now() {
		return LocalDateTime.now(clock);
	}
}
