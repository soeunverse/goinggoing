package com.goinggoing.goinggoing.domain.route.entity;

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
@Table(name = "route_places")
public class RoutePlace {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "route_id", nullable = false)
	private Route route;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "content_id")
	private Content content;

	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "place_type", nullable = false)
	private ContentType placeType;

	private String address;

	private BigDecimal latitude;

	private BigDecimal longitude;

	@Column(name = "day_number", nullable = false)
	private Integer dayNumber;

	@Column(name = "visit_order", nullable = false)
	private Integer visitOrder;

	@Column(name = "estimated_stay_minutes")
	private Integer estimatedStayMinutes;

	@Column(name = "move_minutes_from_previous")
	private Integer moveMinutesFromPrevious;

	@Column(name = "recommendation_note")
	private String recommendationNote;

	protected RoutePlace() {
	}

	public RoutePlace(
			Long id,
			Route route,
			Content content,
			String name,
			ContentType placeType,
			String address,
			BigDecimal latitude,
			BigDecimal longitude,
			Integer dayNumber,
			Integer visitOrder,
			Integer estimatedStayMinutes,
			Integer moveMinutesFromPrevious,
			String recommendationNote
	) {
		this.id = id;
		this.route = route;
		this.content = content;
		this.name = name;
		this.placeType = placeType;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.dayNumber = dayNumber;
		this.visitOrder = visitOrder;
		this.estimatedStayMinutes = estimatedStayMinutes;
		this.moveMinutesFromPrevious = moveMinutesFromPrevious;
		this.recommendationNote = recommendationNote;
	}

	public Long getId() {
		return id;
	}

	public Content getContent() {
		return content;
	}

	public String getName() {
		return name;
	}

	public ContentType getPlaceType() {
		return placeType;
	}

	public String getAddress() {
		return address;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public Integer getDayNumber() {
		return dayNumber;
	}

	public Integer getVisitOrder() {
		return visitOrder;
	}

	public Integer getEstimatedStayMinutes() {
		return estimatedStayMinutes;
	}

	public Integer getMoveMinutesFromPrevious() {
		return moveMinutesFromPrevious;
	}

	public String getRecommendationNote() {
		return recommendationNote;
	}
}
