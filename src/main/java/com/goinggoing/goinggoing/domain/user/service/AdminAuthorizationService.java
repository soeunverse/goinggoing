package com.goinggoing.goinggoing.domain.user.service;

import com.goinggoing.goinggoing.domain.user.entity.User;
import com.goinggoing.goinggoing.domain.user.repository.UserRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthorizationService {

	private final UserRepository userRepository;

	public AdminAuthorizationService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void validateAdmin(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.UNAUTHORIZED);
		}
		if (!user.isAdmin()) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}
	}
}
