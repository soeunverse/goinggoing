package com.goinggoing.goinggoing.domain.content.repository;

import com.goinggoing.goinggoing.domain.content.entity.Content;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content, Long> {

	@Query("""
			SELECT DISTINCT content
			FROM Content content
			LEFT JOIN FETCH content.region
			LEFT JOIN FETCH content.theme
			WHERE content.published = true
			  AND content.deletedAt IS NULL
			  AND (:regionId IS NULL OR content.region.id = :regionId)
			  AND (:themeId IS NULL OR content.theme.id = :themeId)
			  AND (:contentType IS NULL OR content.contentType = :contentType)
			ORDER BY content.hotScore DESC, content.id ASC
			""")
	List<Content> findPublishedContents(
			@Param("regionId") Long regionId,
			@Param("themeId") Long themeId,
			@Param("contentType") ContentType contentType
	);

	@Query("""
			SELECT DISTINCT content
			FROM Content content
			LEFT JOIN FETCH content.region
			LEFT JOIN FETCH content.theme
			LEFT JOIN FETCH content.subTheme
			LEFT JOIN FETCH content.cards
			LEFT JOIN FETCH content.tags
			WHERE content.id = :contentId
			  AND content.published = true
			  AND content.deletedAt IS NULL
			""")
	Optional<Content> findPublishedContent(@Param("contentId") Long contentId);

	@Query("""
			SELECT DISTINCT content
			FROM Content content
			LEFT JOIN FETCH content.region
			LEFT JOIN FETCH content.theme
			WHERE content.published = true
			  AND content.deletedAt IS NULL
			ORDER BY content.hotScore DESC, content.bookmarkCount DESC, content.viewCount DESC, content.id ASC
			""")
	List<Content> findHotContents();
}
