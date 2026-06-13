package com.goinggoing.goinggoing.domain.user.repository;

import com.goinggoing.goinggoing.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository {

	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);

	Optional<User> findById(Long id);

	User save(User user);
}
