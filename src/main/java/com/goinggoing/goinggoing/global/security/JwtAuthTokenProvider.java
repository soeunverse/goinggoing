package com.goinggoing.goinggoing.global.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goinggoing.goinggoing.global.exception.BusinessException;
import com.goinggoing.goinggoing.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtAuthTokenProvider implements AuthTokenProvider {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final String HMAC_ALGORITHM = "HmacSHA256";

	private final String secret;
	private final ZoneId zoneId;

	@Autowired
	public JwtAuthTokenProvider(
			@Value("${auth.jwt.secret:goinggoing-local-jwt-secret-goinggoing-local-jwt-secret}") String secret,
			@Value("${auth.jwt.zone-id:Asia/Seoul}") String zoneId
	) {
		this(secret, ZoneId.of(zoneId));
	}

	JwtAuthTokenProvider(String secret, ZoneId zoneId) {
		this.secret = secret;
		this.zoneId = zoneId;
	}

	@Override
	public String generateAccessToken(Long userId, LocalDateTime expiresAt) {
		try {
			String header = encodeJson(Map.of("alg", "HS256", "typ", "JWT"));
			String payload = encodeJson(Map.of(
					"sub", String.valueOf(userId),
					"exp", expiresAt.atZone(zoneId).toEpochSecond()
			));
			String unsignedToken = header + "." + payload;
			return unsignedToken + "." + sign(unsignedToken);
		} catch (Exception exception) {
			throw new BusinessException(ErrorCode.UNAUTHORIZED);
		}
	}

	@Override
	public String generateRefreshToken() {
		return UUID.randomUUID().toString();
	}

	@Override
	public Long extractUserId(String accessToken) {
		try {
			String[] parts = accessToken.split("\\.");
			if (parts.length != 3 || !MessageDigest.isEqual(sign(parts[0] + "." + parts[1]).getBytes(StandardCharsets.UTF_8),
					parts[2].getBytes(StandardCharsets.UTF_8))) {
				throw new BusinessException(ErrorCode.UNAUTHORIZED);
			}

			Map<String, Object> payload = OBJECT_MAPPER.readValue(decode(parts[1]), new TypeReference<>() {
			});
			long expiresAt = Long.parseLong(String.valueOf(payload.get("exp")));
			if (expiresAt <= Instant.now().getEpochSecond()) {
				throw new BusinessException(ErrorCode.UNAUTHORIZED);
			}
			return Long.valueOf(String.valueOf(payload.get("sub")));
		} catch (BusinessException exception) {
			throw exception;
		} catch (Exception exception) {
			throw new BusinessException(ErrorCode.UNAUTHORIZED);
		}
	}

	private String encodeJson(Map<String, Object> value) throws Exception {
		return Base64.getUrlEncoder()
				.withoutPadding()
				.encodeToString(OBJECT_MAPPER.writeValueAsBytes(value));
	}

	private byte[] decode(String value) {
		return Base64.getUrlDecoder().decode(value);
	}

	private String sign(String value) throws Exception {
		Mac mac = Mac.getInstance(HMAC_ALGORITHM);
		mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
		return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
	}
}
