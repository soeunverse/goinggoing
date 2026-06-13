package com.goinggoing.goinggoing.domain.category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "themes")
public class Theme {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	private String description;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder;

	protected Theme() {
	}

	public Theme(Long id, String name, String description, Integer displayOrder) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.displayOrder = displayOrder;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}
}
