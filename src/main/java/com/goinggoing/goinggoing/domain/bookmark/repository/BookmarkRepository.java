package com.goinggoing.goinggoing.domain.bookmark.repository;

import com.goinggoing.goinggoing.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

	@Query("""
			SELECT bookmark
			FROM Bookmark bookmark
			JOIN FETCH bookmark.content content
			JOIN FETCH content.region
			LEFT JOIN FETCH content.theme
			WHERE bookmark.user.id = :userId
			ORDER BY bookmark.createdAt DESC, bookmark.id DESC
			""")
	List<Bookmark> findAllByUserId(@Param("userId") Long userId);

	boolean existsByUserIdAndContentId(Long userId, Long contentId);

	@Query("""
			SELECT bookmark
			FROM Bookmark bookmark
			JOIN FETCH bookmark.content content
			WHERE bookmark.id = :bookmarkId
			  AND bookmark.user.id = :userId
			""")
	Optional<Bookmark> findByIdAndUserId(@Param("bookmarkId") Long bookmarkId, @Param("userId") Long userId);

	@Query("""
			SELECT bookmark
			FROM Bookmark bookmark
			JOIN FETCH bookmark.content content
			WHERE bookmark.id IN :bookmarkIds
			  AND bookmark.user.id = :userId
			""")
	List<Bookmark> findAllByIdInAndUserId(@Param("bookmarkIds") List<Long> bookmarkIds, @Param("userId") Long userId);
}
