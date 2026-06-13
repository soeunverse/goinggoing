package com.goinggoing.goinggoing.domain.category.repository;

import com.goinggoing.goinggoing.domain.category.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {

	List<Region> findAllByOrderByDisplayOrderAscIdAsc();
}
