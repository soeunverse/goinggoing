package com.goinggoing.goinggoing.domain.user.repository;

import com.goinggoing.goinggoing.domain.user.entity.UserPreference;

import java.util.Optional;

public interface UserPreferenceRepository {

	Optional<UserPreference> findByUserId(Long userId);

	UserPreference save(UserPreference preference);
}
