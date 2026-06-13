package com.goinggoing.goinggoing.domain.route.entity;

import com.goinggoing.goinggoing.domain.category.entity.Theme;
import com.goinggoing.goinggoing.domain.content.entity.Content;
import com.goinggoing.goinggoing.domain.user.entity.TripDurationType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
public class Route {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "content_id", nullable = false)
	private Content content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "theme_id")
	private Theme theme;

	@Column(nullable = false)
	private String title;

	private String summary;

	@Enumerated(EnumType.STRING)
	@Column(name = "trip_duration_type", nullable = false)
	private TripDurationType tripDurationType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RouteStatus status;

	@Column(name = "total_distance_meters")
	private Integer totalDistanceMeters;

	@Column(name = "total_duration_minutes")
	private Integer totalDurationMinutes;

	@Column(name = "map_center_latitude")
	private BigDecimal mapCenterLatitude;

	@Column(name = "map_center_longitude")
	private BigDecimal mapCenterLongitude;

	@OneToMany(mappedBy = "route")
	@OrderBy("dayNumber ASC, visitOrder ASC, id ASC")
	private List<RoutePlace> places = new ArrayList<>();

	protected Route() {
	}

	public Route(
			Long id,
			Content content,
			Theme theme,
			String title,
			String summary,
			TripDurationType tripDurationType,
			RouteStatus status,
			Integer totalDistanceMeters,
			Integer totalDurationMinutes,
			BigDecimal mapCenterLatitude,
			BigDecimal mapCenterLongitude
	) {
		this.id = id;
		this.content = content;
		this.theme = theme;
		this.title = title;
		this.summary = summary;
		this.tripDurationType = tripDurationType;
		this.status = status;
		this.totalDistanceMeters = totalDistanceMeters;
		this.totalDurationMinutes = totalDurationMinutes;
		this.mapCenterLatitude = mapCenterLatitude;
		this.mapCenterLongitude = mapCenterLongitude;
	}

	public void addPlace(RoutePlace place) {
		this.places.add(place);
	}

	public Long getId() {
		return id;
	}

	public Content getContent() {
		return content;
	}

	public String getTitle() {
		return title;
	}

	public String getSummary() {
		return summary;
	}

	public TripDurationType getTripDurationType() {
		return tripDurationType;
	}

	public Integer getTotalDistanceMeters() {
		return totalDistanceMeters;
	}

	public Integer getTotalDurationMinutes() {
		return totalDurationMinutes;
	}

	public BigDecimal getMapCenterLatitude() {
		return mapCenterLatitude;
	}

	public BigDecimal getMapCenterLongitude() {
		return mapCenterLongitude;
	}

	public List<RoutePlace> getPlaces() {
		return places;
	}
}
