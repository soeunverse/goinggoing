package com.goinggoing.goinggoing.domain.sync.entity;

import com.goinggoing.goinggoing.domain.content.entity.Content;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "related_places")
public class RelatedPlace {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "base_content_id", nullable = false)
	private Content baseContent;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "related_content_id")
	private Content relatedContent;

	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "place_type", nullable = false)
	private ContentType placeType;

	@Column(name = "related_rank", nullable = false)
	private Integer relatedRank;

	@Column(name = "relation_score")
	private BigDecimal relationScore;

	private String address;

	private BigDecimal latitude;

	private BigDecimal longitude;

	@Column(name = "source_period")
	private String sourcePeriod;

	@Column(name = "raw_payload", columnDefinition = "JSON")
	private String rawPayload;

	protected RelatedPlace() {
	}

	public RelatedPlace(
			Content baseContent,
			String name,
			ContentType placeType,
			Integer relatedRank,
			BigDecimal relationScore,
			String address,
			BigDecimal latitude,
			BigDecimal longitude,
			String sourcePeriod,
			String rawPayload
	) {
		this.baseContent = baseContent;
		this.name = name;
		this.placeType = placeType;
		this.relatedRank = relatedRank;
		this.relationScore = relationScore;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.sourcePeriod = sourcePeriod;
		this.rawPayload = rawPayload;
	}
}
