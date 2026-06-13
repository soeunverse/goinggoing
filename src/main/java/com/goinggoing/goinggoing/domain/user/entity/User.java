package com.goinggoing.goinggoing.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@Column(nullable = false)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserStatus status;

	protected User() {
	}

	private User(Long id, String email, String passwordHash, String nickname, UserStatus status) {
		this.id = id;
		this.email = email;
		this.passwordHash = passwordHash;
		this.nickname = nickname;
		this.status = status;
	}

	public static User create(String email, String passwordHash, String nickname) {
		return new User(null, email, passwordHash, nickname, UserStatus.ACTIVE);
	}

	public User withId(Long id) {
		return new User(id, email, passwordHash, nickname, status);
	}

	public User withStatus(UserStatus status) {
		return new User(id, email, passwordHash, nickname, status);
	}

	public boolean hasId() {
		return id != null;
	}

	public boolean isActive() {
		return status == UserStatus.ACTIVE;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public String getNickname() {
		return nickname;
	}

	public UserStatus getStatus() {
		return status;
	}
}
