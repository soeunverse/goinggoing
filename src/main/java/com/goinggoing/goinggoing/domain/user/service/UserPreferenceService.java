package com.goinggoing.goinggoing.domain.user.service;

import com.goinggoing.goinggoing.domain.user.dto.UserPreferenceRequest;
import com.goinggoing.goinggoing.domain.user.dto.UserPreferenceResponse;
import com.goinggoing.goinggoing.domain.user.entity.User;
import com.goinggoing.goinggoing.domain.user.entity.UserPreference;
import com.goinggoing.goinggoing.domain.user.repository.UserPreferenceRepository;
import com.goinggoing.goinggoing.domain.user.repository.UserRepository;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserPreferenceService {

	private final UserRepository userRepository;
	private final UserPreferenceRepository userPreferenceRepository;

	public UserPreferenceService(UserRepository userRepository, UserPreferenceRepository userPreferenceRepository) {
		this.userRepository = userRepository;
		this.userPreferenceRepository = userPreferenceRepository;
	}

	public UserPreferenceResponse getMyPreference(Long userId) {
		User user = getActiveUser(userId);
		return userPreferenceRepository.findByUserId(user.getId())
				.map(this::toResponse)
				.orElseGet(UserPreferenceResponse::empty);
	}

	@Transactional
	public UserPreferenceResponse saveMyPreference(Long userId, UserPreferenceRequest request) {
		User user = getActiveUser(userId);
		validateRequest(request);

		UserPreference preference = userPreferenceRepository.findByUserId(user.getId())
				.orElseGet(() -> UserPreference.create(
						user,
						request.preferredTripDuration(),
						request.regionIds(),
						request.themeIds(),
						request.tagIds()
				));
		preference.replace(request.preferredTripDuration(), request.regionIds(), request.themeIds(), request.tagIds());

		return toResponse(userPreferenceRepository.save(preference));
	}

	private void validateRequest(UserPreferenceRequest request) {
		if (request == null || (
				request.preferredTripDuration() == null
						&& isEmpty(request.regionIds())
						&& isEmpty(request.themeIds())
						&& isEmpty(request.tagIds())
		)) {
			throw new BusinessException(ErrorCode.INVALID_PREFERENCE);
		}
	}

	private boolean isEmpty(List<Long> values) {
		return values == null || values.isEmpty();
	}

	private User getActiveUser(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.UNAUTHORIZED);
		}

		return user;
	}

	private UserPreferenceResponse toResponse(UserPreference preference) {
		return new UserPreferenceResponse(
				preference.getPreferredTripDuration(),
				preference.getRegionIds(),
				preference.getThemeIds(),
				preference.getTagIds()
		);
	}
}
