package com.goinggoing.goinggoing.domain.auth.repository;

import com.goinggoing.goinggoing.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long>, RefreshTokenRepository {
}
