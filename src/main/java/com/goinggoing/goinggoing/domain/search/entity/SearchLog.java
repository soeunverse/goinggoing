package com.goinggoing.goinggoing.domain.search.entity;

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
@Table(name = "search_logs")
public class SearchLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false)
	private String keyword;

	@Column(name = "result_count", nullable = false)
	private Integer resultCount;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	protected SearchLog() {
	}

	private SearchLog(User user, String keyword, Integer resultCount, LocalDateTime createdAt) {
		this.user = user;
		this.keyword = keyword;
		this.resultCount = resultCount;
		this.createdAt = createdAt;
	}

	public static SearchLog create(User user, String keyword, Integer resultCount) {
		return new SearchLog(user, keyword, resultCount, LocalDateTime.now());
	}

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public String getKeyword() {
		return keyword;
	}

	public Integer getResultCount() {
		return resultCount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
