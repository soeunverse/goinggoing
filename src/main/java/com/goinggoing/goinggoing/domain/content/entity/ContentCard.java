package com.goinggoing.goinggoing.domain.content.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "content_cards")
public class ContentCard {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "content_id", nullable = false)
	private Content content;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String body;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder;

	protected ContentCard() {
	}

	public ContentCard(Long id, Content content, String title, String body, String imageUrl, Integer displayOrder) {
		this.id = id;
		this.content = content;
		this.title = title;
		this.body = body;
		this.imageUrl = imageUrl;
		this.displayOrder = displayOrder;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}
}
