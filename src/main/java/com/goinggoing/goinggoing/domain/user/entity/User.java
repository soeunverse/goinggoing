package com.goinggoing.goinggoing.domain.user.entity;

public class User {

	private final Long id;
	private final String email;
	private final String passwordHash;
	private final String nickname;
	private final UserStatus status;

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
