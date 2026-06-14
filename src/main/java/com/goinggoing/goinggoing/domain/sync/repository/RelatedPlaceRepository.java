package com.goinggoing.goinggoing.domain.sync.repository;

import com.goinggoing.goinggoing.domain.sync.entity.RelatedPlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelatedPlaceRepository extends JpaRepository<RelatedPlace, Long> {

	void deleteByBaseContentId(Long baseContentId);
}
