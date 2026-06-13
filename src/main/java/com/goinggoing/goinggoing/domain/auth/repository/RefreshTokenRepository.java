package com.goinggoing.goinggoing.domain.auth.repository;

import com.goinggoing.goinggoing.domain.auth.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

	Optional<RefreshToken> findByToken(String token);

	RefreshToken save(RefreshToken refreshToken);
}
