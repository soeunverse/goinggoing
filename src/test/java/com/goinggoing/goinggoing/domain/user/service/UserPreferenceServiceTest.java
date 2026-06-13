package com.goinggoing.goinggoing.domain.user.service;

import com.goinggoing.goinggoing.domain.user.dto.UserPreferenceRequest;
import com.goinggoing.goinggoing.domain.user.dto.UserPreferenceResponse;
import com.goinggoing.goinggoing.domain.user.entity.TripDurationType;
import com.goinggoing.goinggoing.domain.user.entity.User;
import com.goinggoing.goinggoing.domain.user.entity.UserPreference;
import com.goinggoing.goinggoing.domain.user.entity.UserStatus;
import com.goinggoing.goinggoing.domain.user.repository.UserPreferenceRepository;
import com.goinggoing.goinggoing.domain.user.repository.UserRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserPreferenceServiceTest {

	private FakeUserRepository userRepository;
	private FakeUserPreferenceRepository userPreferenceRepository;
	private UserPreferenceService userPreferenceService;

	@BeforeEach
	void setUp() {
		userRepository = new FakeUserRepository();
		userPreferenceRepository = new FakeUserPreferenceRepository();
		userPreferenceService = new UserPreferenceService(userRepository, userPreferenceRepository);
	}

	@Test
	@DisplayName("저장된 온보딩 취향을 조회한다")
	void getPreferenceSuccess() {
		User user = userRepository.save(User.create("user@example.com", "encoded-password", "즉흥여행자"));
		userPreferenceRepository.save(UserPreference.create(
				user,
				TripDurationType.DAY_TRIP,
				List.of(1L, 2L),
				List.of(10L),
				List.of(100L, 101L)
		));

		UserPreferenceResponse response = userPreferenceService.getMyPreference(user.getId());

		assertThat(response.preferredTripDuration()).isEqualTo(TripDurationType.DAY_TRIP);
		assertThat(response.regionIds()).containsExactly(1L, 2L);
		assertThat(response.themeIds()).containsExactly(10L);
		assertThat(response.tagIds()).containsExactly(100L, 101L);
	}

	@Test
	@DisplayName("저장된 온보딩 취향이 없으면 빈 응답을 반환한다")
	void getEmptyPreferenceSuccess() {
		User user = userRepository.save(User.create("user@example.com", "encoded-password", "즉흥여행자"));

		UserPreferenceResponse response = userPreferenceService.getMyPreference(user.getId());

		assertThat(response.preferredTripDuration()).isNull();
		assertThat(response.regionIds()).isEmpty();
		assertThat(response.themeIds()).isEmpty();
		assertThat(response.tagIds()).isEmpty();
	}

	@Test
	@DisplayName("온보딩 취향이 없으면 새로 저장한다")
	void savePreferenceCreatesNewPreference() {
		User user = userRepository.save(User.create("user@example.com", "encoded-password", "즉흥여행자"));
		UserPreferenceRequest request = new UserPreferenceRequest(
				TripDurationType.ONE_NIGHT_TWO_DAYS,
				List.of(1L, 1L, 2L),
				List.of(10L),
				List.of(100L)
		);

		UserPreferenceResponse response = userPreferenceService.saveMyPreference(user.getId(), request);

		assertThat(response.preferredTripDuration()).isEqualTo(TripDurationType.ONE_NIGHT_TWO_DAYS);
		assertThat(response.regionIds()).containsExactly(1L, 2L);
		assertThat(response.themeIds()).containsExactly(10L);
		assertThat(response.tagIds()).containsExactly(100L);
	}

	@Test
	@DisplayName("온보딩 취향이 있으면 기존 값을 교체한다")
	void savePreferenceUpdatesExistingPreference() {
		User user = userRepository.save(User.create("user@example.com", "encoded-password", "즉흥여행자"));
		userPreferenceRepository.save(UserPreference.create(
				user,
				TripDurationType.DAY_TRIP,
				List.of(1L),
				List.of(10L),
				List.of(100L)
		));
		UserPreferenceRequest request = new UserPreferenceRequest(
				TripDurationType.ONE_NIGHT_TWO_DAYS,
				List.of(3L),
				List.of(11L, 12L),
				List.of()
		);

		UserPreferenceResponse response = userPreferenceService.saveMyPreference(user.getId(), request);

		assertThat(response.preferredTripDuration()).isEqualTo(TripDurationType.ONE_NIGHT_TWO_DAYS);
		assertThat(response.regionIds()).containsExactly(3L);
		assertThat(response.themeIds()).containsExactly(11L, 12L);
		assertThat(response.tagIds()).isEmpty();
	}

	@Test
	@DisplayName("사용자가 없으면 USER_NOT_FOUND 예외가 발생한다")
	void missingUserFails() {
		assertThatThrownBy(() -> userPreferenceService.getMyPreference(999L))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));
	}

	@Test
	@DisplayName("비활성 사용자는 온보딩 취향을 저장할 수 없다")
	void inactiveUserFails() {
		User user = userRepository.save(User.create("user@example.com", "encoded-password", "즉흥여행자")
				.withStatus(UserStatus.DELETED));
		UserPreferenceRequest request = new UserPreferenceRequest(TripDurationType.DAY_TRIP, List.of(1L), List.of(), List.of());

		assertThatThrownBy(() -> userPreferenceService.saveMyPreference(user.getId(), request))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED));
	}

	@Test
	@DisplayName("비어 있는 온보딩 취향 요청은 INVALID_PREFERENCE 예외가 발생한다")
	void emptyPreferenceRequestFails() {
		User user = userRepository.save(User.create("user@example.com", "encoded-password", "즉흥여행자"));
		UserPreferenceRequest request = new UserPreferenceRequest(null, List.of(), List.of(), List.of());

		assertThatThrownBy(() -> userPreferenceService.saveMyPreference(user.getId(), request))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PREFERENCE));
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

	private static class FakeUserPreferenceRepository implements UserPreferenceRepository {

		private final Map<Long, UserPreference> preferences = new HashMap<>();
		private long sequence = 1L;

		@Override
		public Optional<UserPreference> findByUserId(Long userId) {
			return preferences.values().stream()
					.filter(preference -> preference.getUser().getId().equals(userId))
					.findFirst();
		}

		@Override
		public UserPreference save(UserPreference preference) {
			UserPreference savedPreference = preference.hasId() ? preference : preference.withId(sequence++);
			preferences.put(savedPreference.getId(), savedPreference);
			return savedPreference;
		}
	}
}
