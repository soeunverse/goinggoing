package com.goinggoing.goinggoing.domain.user.service;

import com.goinggoing.goinggoing.domain.user.dto.UserProfileResponse;
import com.goinggoing.goinggoing.domain.user.dto.UserProfileUpdateRequest;
import com.goinggoing.goinggoing.domain.user.entity.User;
import com.goinggoing.goinggoing.domain.user.repository.UserRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserProfileService {

	private final UserRepository userRepository;

	public UserProfileService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public UserProfileResponse getMyProfile(Long userId) {
		// 사용자 ID로 계정 조회
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		// 활성 계정 검증
		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.UNAUTHORIZED);
		}

		// 내 정보 응답 생성
		return new UserProfileResponse(
				user.getId(),
				user.getEmail(),
				user.getNickname(),
				user.getStatus()
		);
	}

	@Transactional
	public UserProfileResponse updateMyProfile(Long userId, UserProfileUpdateRequest request) {
		// 닉네임 형식 검증
		validateNickname(request.nickname());
		// 수정 대상 사용자 조회
		User user = getActiveUser(userId);

		// 닉네임 변경
		user.updateNickname(request.nickname().trim());

		// 수정 결과 응답 생성
		return toProfileResponse(user);
	}

	@Transactional
	public void withdraw(Long userId) {
		// 탈퇴 대상 사용자 조회
		User user = getActiveUser(userId);
		// soft delete 처리
		user.withdraw();
	}

	private void validateNickname(String nickname) {
		if (nickname == null || nickname.isBlank()) {
			throw new BusinessException(ErrorCode.INVALID_NICKNAME);
		}
	}

	private User getActiveUser(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.UNAUTHORIZED);
		}

		return user;
	}

	private UserProfileResponse toProfileResponse(User user) {
		return new UserProfileResponse(
				user.getId(),
				user.getEmail(),
				user.getNickname(),
				user.getStatus()
		);
	}
}
