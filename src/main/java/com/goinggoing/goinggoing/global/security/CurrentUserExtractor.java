package com.goinggoing.goinggoing.global.security;

import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserExtractor {

	private static final String BEARER_PREFIX = "Bearer ";

	private final AuthTokenProvider authTokenProvider;

	public CurrentUserExtractor(AuthTokenProvider authTokenProvider) {
		this.authTokenProvider = authTokenProvider;
	}

	public Long extractUserId(String authorizationHeader) {
		if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
			throw new BusinessException(ErrorCode.UNAUTHORIZED);
		}

		return authTokenProvider.extractUserId(authorizationHeader.substring(BEARER_PREFIX.length()));
	}
}
