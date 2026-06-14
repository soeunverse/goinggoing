package com.goinggoing.goinggoing.domain.sync.entity;

import com.goinggoing.goinggoing.domain.category.entity.Region;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "regional_demand_metrics")
public class RegionalDemandMetric {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "region_id", nullable = false)
	private Region region;

	@Column(name = "metric_month", nullable = false)
	private String metricMonth;

	@Column(name = "service_demand_score")
	private BigDecimal serviceDemandScore;

	@Column(name = "cultural_resource_score")
	private BigDecimal culturalResourceScore;

	@Column(name = "navigation_search_score")
	private BigDecimal navigationSearchScore;

	@Column(name = "raw_payload", columnDefinition = "JSON")
	private String rawPayload;

	protected RegionalDemandMetric() {
	}

	private RegionalDemandMetric(Region region, String metricMonth) {
		this.region = region;
		this.metricMonth = metricMonth;
	}

	public static RegionalDemandMetric create(Region region, String metricMonth) {
		return new RegionalDemandMetric(region, metricMonth);
	}

	public void updateServiceDemand(BigDecimal serviceDemandScore, BigDecimal navigationSearchScore, String rawPayload) {
		this.serviceDemandScore = serviceDemandScore;
		this.navigationSearchScore = navigationSearchScore;
		this.rawPayload = rawPayload;
	}

	public void updateCulturalResource(BigDecimal culturalResourceScore, BigDecimal navigationSearchScore, String rawPayload) {
		this.culturalResourceScore = culturalResourceScore;
		this.navigationSearchScore = navigationSearchScore;
		this.rawPayload = rawPayload;
	}

	public Long getId() {
		return id;
	}

	public Region getRegion() {
		return region;
	}

	public String getMetricMonth() {
		return metricMonth;
	}
}
