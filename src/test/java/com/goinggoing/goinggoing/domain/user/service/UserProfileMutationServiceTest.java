package com.goinggoing.goinggoing.domain.user.service;

import com.goinggoing.goinggoing.domain.user.dto.UserProfileResponse;
import com.goinggoing.goinggoing.domain.user.dto.UserProfileUpdateRequest;
import com.goinggoing.goinggoing.domain.user.entity.User;
import com.goinggoing.goinggoing.domain.user.entity.UserStatus;
import com.goinggoing.goinggoing.domain.user.repository.UserRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserProfileMutationServiceTest {

	private FakeUserRepository userRepository;
	private UserProfileService userProfileService;

	@BeforeEach
	void setUp() {
		userRepository = new FakeUserRepository();
		userProfileService = new UserProfileService(userRepository);
	}

	@Test
	@DisplayName("내 정보 수정 성공 시 닉네임을 변경한다")
	void updateMyProfileSuccess() {
		User user = userRepository.save(User.create("user@example.com", "encoded-password", "기존닉네임"));

		UserProfileResponse response = userProfileService.updateMyProfile(
				user.getId(),
				new UserProfileUpdateRequest("새닉네임")
		);

		assertThat(response.userId()).isEqualTo(user.getId());
		assertThat(response.email()).isEqualTo("user@example.com");
		assertThat(response.nickname()).isEqualTo("새닉네임");
		assertThat(response.status()).isEqualTo(UserStatus.ACTIVE);
		assertThat(userRepository.findById(user.getId()).orElseThrow().getNickname()).isEqualTo("새닉네임");
	}

	@Test
	@DisplayName("닉네임이 공백이면 INVALID_NICKNAME 예외가 발생한다")
	void blankNicknameUpdateFails() {
		User user = userRepository.save(User.create("user@example.com", "encoded-password", "기존닉네임"));

		assertThatThrownBy(() -> userProfileService.updateMyProfile(user.getId(), new UserProfileUpdateRequest(" ")))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_NICKNAME));
	}

	@Test
	@DisplayName("존재하지 않는 사용자 수정은 USER_NOT_FOUND 예외가 발생한다")
	void updateMissingUserFails() {
		assertThatThrownBy(() -> userProfileService.updateMyProfile(999L, new UserProfileUpdateRequest("새닉네임")))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));
	}

	@Test
	@DisplayName("비활성 사용자는 내 정보를 수정할 수 없다")
	void inactiveUserUpdateFails() {
		User user = userRepository.save(User.create("user@example.com", "encoded-password", "기존닉네임")
				.withStatus(UserStatus.DELETED));

		assertThatThrownBy(() -> userProfileService.updateMyProfile(user.getId(), new UserProfileUpdateRequest("새닉네임")))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED));
	}

	@Test
	@DisplayName("회원 탈퇴 성공 시 사용자를 DELETED 상태로 변경한다")
	void withdrawSuccess() {
		User user = userRepository.save(User.create("user@example.com", "encoded-password", "즉흥여행자"));

		userProfileService.withdraw(user.getId());

		User withdrawnUser = userRepository.findById(user.getId()).orElseThrow();
		assertThat(withdrawnUser.getStatus()).isEqualTo(UserStatus.DELETED);
		assertThat(withdrawnUser.isActive()).isFalse();
	}

	@Test
	@DisplayName("존재하지 않는 사용자 탈퇴는 USER_NOT_FOUND 예외가 발생한다")
	void withdrawMissingUserFails() {
		assertThatThrownBy(() -> userProfileService.withdraw(999L))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));
	}

	@Test
	@DisplayName("이미 비활성화된 사용자는 탈퇴할 수 없다")
	void inactiveUserWithdrawFails() {
		User user = userRepository.save(User.create("user@example.com", "encoded-password", "즉흥여행자")
				.withStatus(UserStatus.DELETED));

		assertThatThrownBy(() -> userProfileService.withdraw(user.getId()))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED));
	}

	private static class FakeUserRepository implements UserRepository {

		private final Map<Long, User> users = new HashMap<>();
		private long sequence = 1L;

		@Override
		public boolean existsByEmail(String email) {
			return users.values().stream().anyMatch(user -> user.getEmail().equals(email));
		}

		@Override
		public Optional<User> findByEmail(String email) {
			return users.values().stream()
					.filter(user -> user.getEmail().equals(email))
					.findFirst();
		}

		@Override
		public Optional<User> findById(Long id) {
			return Optional.ofNullable(users.get(id));
		}

		@Override
		public User save(User user) {
			User savedUser = user.hasId() ? user : user.withId(sequence++);
			users.put(savedUser.getId(), savedUser);
			return savedUser;
		}
	}
}
