package com.goinggoing.goinggoing.domain.bookmark.entity;

import com.goinggoing.goinggoing.domain.content.entity.Content;
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
@Table(name = "bookmarks")
public class Bookmark {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "content_id", nullable = false)
	private Content content;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	protected Bookmark() {
	}

	public Bookmark(Long id, User user, Content content, LocalDateTime createdAt) {
		this.id = id;
		this.user = user;
		this.content = content;
		this.createdAt = createdAt;
	}

	public static Bookmark create(User user, Content content) {
		return new Bookmark(null, user, content, LocalDateTime.now());
	}

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public Content getContent() {
		return content;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
