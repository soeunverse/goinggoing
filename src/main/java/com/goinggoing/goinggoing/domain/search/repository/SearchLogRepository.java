package com.goinggoing.goinggoing.domain.search.repository;

import com.goinggoing.goinggoing.domain.search.dto.PopularSearchKeywordResponse;
import com.goinggoing.goinggoing.domain.search.dto.RecentSearchKeywordResponse;
import com.goinggoing.goinggoing.domain.search.entity.SearchLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {

	@Query("""
			SELECT new com.goinggoing.goinggoing.domain.search.dto.PopularSearchKeywordResponse(
				searchLog.keyword,
				COUNT(searchLog.id)
			)
			FROM SearchLog searchLog
			GROUP BY searchLog.keyword
			ORDER BY COUNT(searchLog.id) DESC, MAX(searchLog.createdAt) DESC
			""")
	List<PopularSearchKeywordResponse> findPopularKeywords(Pageable pageable);

	default List<PopularSearchKeywordResponse> findPopularKeywords(int limit) {
		return findPopularKeywords(Pageable.ofSize(limit));
	}

	@Query("""
			SELECT new com.goinggoing.goinggoing.domain.search.dto.RecentSearchKeywordResponse(
				searchLog.keyword,
				MAX(searchLog.createdAt)
			)
			FROM SearchLog searchLog
			WHERE searchLog.user.id = :userId
			GROUP BY searchLog.keyword
			ORDER BY MAX(searchLog.createdAt) DESC
			""")
	List<RecentSearchKeywordResponse> findRecentKeywordsByUserId(Long userId, Pageable pageable);

	default List<RecentSearchKeywordResponse> findRecentKeywordsByUserId(Long userId, int limit) {
		return findRecentKeywordsByUserId(userId, Pageable.ofSize(limit));
	}
}
