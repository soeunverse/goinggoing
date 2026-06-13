package com.goinggoing.goinggoing.domain.category.dto;

public record ThemeResponse(
		Long id,
		String name,
		String description,
		Integer displayOrder,
		Long contentCount
) {
}
