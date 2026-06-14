package com.goinggoing.goinggoing.domain.sync.repository;

import com.goinggoing.goinggoing.domain.sync.entity.RegionalDemandMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionalDemandMetricRepository extends JpaRepository<RegionalDemandMetric, Long> {

	Optional<RegionalDemandMetric> findByRegionIdAndMetricMonth(Long regionId, String metricMonth);
}
