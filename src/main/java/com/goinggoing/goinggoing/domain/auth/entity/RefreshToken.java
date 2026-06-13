package com.goinggoing.goinggoing.domain.auth.entity;

import com.goinggoing.goinggoing.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, unique = true, length = 512)
	private String token;

	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;

	@Column(name = "revoked_at")
	private LocalDateTime revokedAt;

	protected RefreshToken() {
	}

	private RefreshToken(Long id, User user, String token, LocalDateTime expiresAt, LocalDateTime revokedAt) {
		this.id = id;
		this.user = user;
		this.token = token;
		this.expiresAt = expiresAt;
		this.revokedAt = revokedAt;
	}

	public static RefreshToken issue(User user, String token, LocalDateTime expiresAt) {
		return new RefreshToken(null, user, token, expiresAt, null);
	}

	public RefreshToken withId(Long id) {
		return new RefreshToken(id, user, token, expiresAt, revokedAt);
	}

	public void revoke(LocalDateTime revokedAt) {
		this.revokedAt = revokedAt;
	}

	public boolean isAvailable(LocalDateTime now) {
		return !isRevoked() && expiresAt.isAfter(now);
	}

	public boolean hasId() {
		return id != null;
	}

	public boolean isRevoked() {
		return revokedAt != null;
	}

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public String getToken() {
		return token;
	}
}
