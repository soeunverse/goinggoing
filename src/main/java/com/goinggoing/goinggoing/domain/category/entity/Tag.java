package com.goinggoing.goinggoing.domain.category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tags")
public class Tag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder;

	protected Tag() {
	}

	public Tag(Long id, String name, Integer displayOrder) {
		this.id = id;
		this.name = name;
		this.displayOrder = displayOrder;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}
}
