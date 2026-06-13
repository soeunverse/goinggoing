package com.goinggoing.goinggoing.domain.route.repository;

import com.goinggoing.goinggoing.domain.route.entity.Route;
import com.goinggoing.goinggoing.domain.user.entity.TripDurationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {

	@Query("""
			SELECT DISTINCT route
			FROM Route route
			JOIN FETCH route.content content
			LEFT JOIN FETCH route.places
			WHERE route.status = 'PUBLISHED'
			  AND content.id = :contentId
			  AND route.tripDurationType = :tripDurationType
			ORDER BY route.id ASC
			""")
	Optional<Route> findFirstByContentIdAndTripDurationType(
			@Param("contentId") Long contentId,
			@Param("tripDurationType") TripDurationType tripDurationType
	);

	@Query("""
			SELECT DISTINCT route
			FROM Route route
			JOIN FETCH route.content
			LEFT JOIN FETCH route.places
			WHERE route.status = 'PUBLISHED'
			  AND route.id = :routeId
			""")
	Optional<Route> findPublishedRoute(@Param("routeId") Long routeId);

	@Query("""
			SELECT DISTINCT route
			FROM Route route
			JOIN FETCH route.content content
			LEFT JOIN FETCH route.places
			WHERE route.status = 'PUBLISHED'
			  AND content.region.id = :regionId
			ORDER BY route.id ASC
			""")
	List<Route> findPublishedRoutesByRegionId(@Param("regionId") Long regionId);
}
