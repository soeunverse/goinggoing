package com.goinggoing.goinggoing.domain.user.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Entity
@Table(name = "user_preferences")
public class UserPreference {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "preferred_trip_duration")
	private TripDurationType preferredTripDuration;

	@ElementCollection
	@CollectionTable(name = "user_preference_regions", joinColumns = @JoinColumn(name = "user_preference_id"))
	@Column(name = "region_id", nullable = false)
	private List<Long> regionIds = new ArrayList<>();

	@ElementCollection
	@CollectionTable(name = "user_preference_themes", joinColumns = @JoinColumn(name = "user_preference_id"))
	@Column(name = "theme_id", nullable = false)
	private List<Long> themeIds = new ArrayList<>();

	@ElementCollection
	@CollectionTable(name = "user_preference_tags", joinColumns = @JoinColumn(name = "user_preference_id"))
	@Column(name = "tag_id", nullable = false)
	private List<Long> tagIds = new ArrayList<>();

	protected UserPreference() {
	}

	private UserPreference(
			Long id,
			User user,
			TripDurationType preferredTripDuration,
			List<Long> regionIds,
			List<Long> themeIds,
			List<Long> tagIds
	) {
		this.id = id;
		this.user = user;
		replace(preferredTripDuration, regionIds, themeIds, tagIds);
	}

	public static UserPreference create(
			User user,
			TripDurationType preferredTripDuration,
			List<Long> regionIds,
			List<Long> themeIds,
			List<Long> tagIds
	) {
		return new UserPreference(null, user, preferredTripDuration, regionIds, themeIds, tagIds);
	}

	public UserPreference withId(Long id) {
		return new UserPreference(id, user, preferredTripDuration, regionIds, themeIds, tagIds);
	}

	public void replace(
			TripDurationType preferredTripDuration,
			List<Long> regionIds,
			List<Long> themeIds,
			List<Long> tagIds
	) {
		this.preferredTripDuration = preferredTripDuration;
		this.regionIds = distinct(regionIds);
		this.themeIds = distinct(themeIds);
		this.tagIds = distinct(tagIds);
	}

	public boolean hasId() {
		return id != null;
	}

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public TripDurationType getPreferredTripDuration() {
		return preferredTripDuration;
	}

	public List<Long> getRegionIds() {
		return List.copyOf(regionIds);
	}

	public List<Long> getThemeIds() {
		return List.copyOf(themeIds);
	}

	public List<Long> getTagIds() {
		return List.copyOf(tagIds);
	}

	private static List<Long> distinct(List<Long> ids) {
		if (ids == null) {
			return new ArrayList<>();
		}
		return new ArrayList<>(new LinkedHashSet<>(ids));
	}
}
