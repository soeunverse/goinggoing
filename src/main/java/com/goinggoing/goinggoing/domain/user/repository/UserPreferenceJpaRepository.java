package com.goinggoing.goinggoing.domain.user.repository;

import com.goinggoing.goinggoing.domain.user.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferenceJpaRepository extends JpaRepository<UserPreference, Long>, UserPreferenceRepository {
}
