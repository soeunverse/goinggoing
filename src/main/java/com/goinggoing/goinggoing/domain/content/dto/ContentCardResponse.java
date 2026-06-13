package com.goinggoing.goinggoing.domain.content.dto;

public record ContentCardResponse(
		Long id,
		String title,
		String body,
		String imageUrl,
		Integer displayOrder
) {
}
