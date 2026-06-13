package com.goinggoing.goinggoing.domain.user.service;

import com.goinggoing.goinggoing.domain.user.dto.UserSignupRequest;
import com.goinggoing.goinggoing.domain.user.dto.UserSignupResponse;
import com.goinggoing.goinggoing.domain.user.entity.User;
import com.goinggoing.goinggoing.domain.user.repository.UserRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;

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
		validateEmail(request.email());
		validatePassword(request.password());
		validateNickname(request.nickname());

		String passwordHash = passwordEncoder.encode(request.password());
		User user = User.create(request.email(), passwordHash, request.nickname().trim());
		User savedUser = userRepository.save(user);

		return new UserSignupResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname());
	}

	private void validateEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}
	}

	private void validatePassword(String password) {
		if (password == null || password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
			throw new BusinessException(ErrorCode.INVALID_PASSWORD);
		}
		if (!password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
			throw new BusinessException(ErrorCode.INVALID_PASSWORD);
		}
	}

	private void validateNickname(String nickname) {
		if (nickname == null || nickname.isBlank()) {
			throw new BusinessException(ErrorCode.INVALID_NICKNAME);
		}
	}
}
