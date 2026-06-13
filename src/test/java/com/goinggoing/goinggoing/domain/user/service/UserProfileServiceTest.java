package com.goinggoing.goinggoing.domain.user.service;

import com.goinggoing.goinggoing.domain.user.dto.UserProfileResponse;
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

class UserProfileServiceTest {

	private FakeUserRepository userRepository;
	private UserProfileService userProfileService;

	@BeforeEach
	void setUp() {
		userRepository = new FakeUserRepository();
		userProfileService = new UserProfileService(userRepository);
	}

	@Test
	@DisplayName("활성 사용자의 내 정보 조회에 성공한다")
	void getMyProfileSuccess() {
		User user = userRepository.save(User.create("user@example.com", "encoded-password", "즉흥여행자"));

		UserProfileResponse response = userProfileService.getMyProfile(user.getId());

		assertThat(response.userId()).isEqualTo(user.getId());
		assertThat(response.email()).isEqualTo("user@example.com");
		assertThat(response.nickname()).isEqualTo("즉흥여행자");
		assertThat(response.status()).isEqualTo(UserStatus.ACTIVE);
	}

	@Test
	@DisplayName("존재하지 않는 사용자 ID면 USER_NOT_FOUND 예외가 발생한다")
	void userNotFoundFails() {
		assertThatThrownBy(() -> userProfileService.getMyProfile(999L))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));
	}

	@Test
	@DisplayName("비활성 사용자는 내 정보 조회를 할 수 없다")
	void inactiveUserFails() {
		User user = userRepository.save(User.create("user@example.com", "encoded-password", "즉흥여행자")
				.withStatus(UserStatus.DELETED));

		assertThatThrownBy(() -> userProfileService.getMyProfile(user.getId()))
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
