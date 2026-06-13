package com.goinggoing.goinggoing.domain.content.dto;

public record ContentManagementCardRequest(
		String title,
		String body,
		String imageUrl,
		Integer displayOrder
) {
}
