package com.goinggoing.goinggoing.domain.category.entity;

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
@Table(name = "sub_themes")
public class SubTheme {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "theme_id", nullable = false)
	private Theme theme;

	@Column(nullable = false)
	private String name;

	private String description;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder;

	protected SubTheme() {
	}

	public SubTheme(Long id, Theme theme, String name, String description, Integer displayOrder) {
		this.id = id;
		this.theme = theme;
		this.name = name;
		this.description = description;
		this.displayOrder = displayOrder;
	}

	public Long getId() {
		return id;
	}

	public Theme getTheme() {
		return theme;
	}

	public String getName() {
		return name;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}
}
