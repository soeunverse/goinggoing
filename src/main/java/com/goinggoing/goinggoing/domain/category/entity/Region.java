package com.goinggoing.goinggoing.domain.category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "regions")
public class Region {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "area_code")
	private String areaCode;

	@Column(nullable = false)
	private String name;

	@Column(name = "full_name", nullable = false)
	private String fullName;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder;

	protected Region() {
	}

	public Region(Long id, String areaCode, String name, String fullName, Integer displayOrder) {
		this.id = id;
		this.areaCode = areaCode;
		this.name = name;
		this.fullName = fullName;
		this.displayOrder = displayOrder;
	}

	public Long getId() {
		return id;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public String getName() {
		return name;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}
}
