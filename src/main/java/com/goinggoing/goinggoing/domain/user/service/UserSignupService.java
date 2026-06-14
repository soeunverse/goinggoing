package com.goinggoing.goinggoing.domain.user.service;

import com.goinggoing.goinggoing.domain.user.dto.UserSignupRequest;
import com.goinggoing.goinggoing.domain.user.dto.UserSignupResponse;
import com.goinggoing.goinggoing.domain.user.entity.User;
import com.goinggoing.goinggoing.domain.user.repository.UserRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserSignupService {

	private static final int MIN_PASSWORD_LENGTH = 8;
	private static final int MAX_PASSWORD_LENGTH = 100;

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserSignupService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public UserSignupResponse signup(UserSignupRequest request) {
		// 회원가입 입력값 검증
		validateEmail(request.email());
		validatePassword(request.password());
		validateNickname(request.nickname());

		// 비밀번호 암호화 후 사용자 생성
		String passwordHash = passwordEncoder.encode(request.password());
		User user = User.create(request.email(), passwordHash, request.nickname().trim());
		User savedUser = userRepository.save(user);
		log.info("[DB 저장] 회원가입 userId={} email={} nickname={}", savedUser.getId(), savedUser.getEmail(), savedUser.getNickname());

		// 회원가입 응답 생성
		return new UserSignupResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname());
	}

	private void validateEmail(String email) {
		// 이메일 중복 검증
		if (userRepository.existsByEmail(email)) {
			throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}
	}

	private void validatePassword(String password) {
		// 비밀번호 길이 검증
		if (password == null || password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
			throw new BusinessException(ErrorCode.INVALID_PASSWORD);
		}
		// 비밀번호 영문/숫자 포함 검증
		if (!password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
			throw new BusinessException(ErrorCode.INVALID_PASSWORD);
		}
	}

	private void validateNickname(String nickname) {
		// 닉네임 공백 검증
		if (nickname == null || nickname.isBlank()) {
			throw new BusinessException(ErrorCode.INVALID_NICKNAME);
		}
	}
}
