package com.goinggoing.goinggoing.domain.user.repository;

import com.goinggoing.goinggoing.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long>, UserRepository {
}
