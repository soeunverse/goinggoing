package com.goinggoing.goinggoing.domain.content.entity;

import com.goinggoing.goinggoing.domain.category.entity.Region;
import com.goinggoing.goinggoing.domain.category.entity.SubTheme;
import com.goinggoing.goinggoing.domain.category.entity.Tag;
import com.goinggoing.goinggoing.domain.category.entity.Theme;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "contents")
public class Content {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "region_id", nullable = false)
	private Region region;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "theme_id")
	private Theme theme;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sub_theme_id")
	private SubTheme subTheme;

	@Column(nullable = false)
	private String title;

	@Enumerated(EnumType.STRING)
	@Column(name = "content_type", nullable = false)
	private ContentType contentType;

	private String summary;

	@Column(columnDefinition = "TEXT")
	private String description;

	private String address;

	private BigDecimal latitude;

	private BigDecimal longitude;

	@Column(name = "thumbnail_url")
	private String thumbnailUrl;

	@Enumerated(EnumType.STRING)
	@Column(name = "source_type", nullable = false)
	private ContentSourceType sourceType = ContentSourceType.ADMIN;

	@Column(name = "view_count", nullable = false)
	private Long viewCount;

	@Column(name = "bookmark_count", nullable = false)
	private Long bookmarkCount;

	@Column(name = "hot_score", nullable = false)
	private BigDecimal hotScore;

	@Column(name = "is_published", nullable = false)
	private boolean published;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@OneToMany(mappedBy = "content", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
	@OrderBy("displayOrder ASC, id ASC")
	private List<ContentCard> cards = new ArrayList<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "content_tags",
			joinColumns = @JoinColumn(name = "content_id"),
			inverseJoinColumns = @JoinColumn(name = "tag_id")
	)
	@OrderBy("displayOrder ASC, id ASC")
	private Set<Tag> tags = new LinkedHashSet<>();

	protected Content() {
	}

	public Content(
			Long id,
			Region region,
			Theme theme,
			SubTheme subTheme,
			String title,
			ContentType contentType,
			String summary,
			String description,
			String address,
			BigDecimal latitude,
			BigDecimal longitude,
			String thumbnailUrl,
			Long viewCount,
			Long bookmarkCount,
			BigDecimal hotScore,
			boolean published
	) {
		this.id = id;
		this.region = region;
		this.theme = theme;
		this.subTheme = subTheme;
		this.title = title;
		this.contentType = contentType;
		this.summary = summary;
		this.description = description;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.thumbnailUrl = thumbnailUrl;
		this.viewCount = viewCount;
		this.bookmarkCount = bookmarkCount;
		this.hotScore = hotScore;
		this.published = published;
	}

	public static Content createAdminContent(
			Region region,
			Theme theme,
			SubTheme subTheme,
			String title,
			ContentType contentType,
			String summary,
			String description,
			String address,
			BigDecimal latitude,
			BigDecimal longitude,
			String thumbnailUrl,
			boolean published
	) {
		return new Content(
				null,
				region,
				theme,
				subTheme,
				title,
				contentType,
				summary,
				description,
				address,
				latitude,
				longitude,
				thumbnailUrl,
				0L,
				0L,
				BigDecimal.ZERO,
				published
		);
	}

	public void update(
			Region region,
			Theme theme,
			SubTheme subTheme,
			String title,
			ContentType contentType,
			String summary,
			String description,
			String address,
			BigDecimal latitude,
			BigDecimal longitude,
			String thumbnailUrl,
			boolean published
	) {
		this.region = region;
		this.theme = theme;
		this.subTheme = subTheme;
		this.title = title;
		this.contentType = contentType;
		this.summary = summary;
		this.description = description;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.thumbnailUrl = thumbnailUrl;
		this.published = published;
	}

	public void increaseViewCount() {
		this.viewCount += 1;
		this.hotScore = this.hotScore.add(BigDecimal.ONE);
	}

	public void increaseBookmarkCount() {
		this.bookmarkCount += 1;
		this.hotScore = this.hotScore.add(BigDecimal.ONE);
	}

	public void decreaseBookmarkCount() {
		if (this.bookmarkCount > 0) {
			this.bookmarkCount -= 1;
		}
	}

	public void addCard(ContentCard card) {
		this.cards.add(card);
	}

	public void addTag(Tag tag) {
		this.tags.add(tag);
	}

	public void replaceCards(List<ContentCard> cards) {
		this.cards.clear();
		this.cards.addAll(cards);
	}

	public void replaceTags(List<Tag> tags) {
		this.tags.clear();
		this.tags.addAll(tags);
	}

	public void softDelete(LocalDateTime deletedAt) {
		this.published = false;
		this.deletedAt = deletedAt;
	}

	public Long getId() {
		return id;
	}

	public Region getRegion() {
		return region;
	}

	public Theme getTheme() {
		return theme;
	}

	public SubTheme getSubTheme() {
		return subTheme;
	}

	public String getTitle() {
		return title;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public String getSummary() {
		return summary;
	}

	public String getDescription() {
		return description;
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

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public Long getViewCount() {
		return viewCount;
	}

	public Long getBookmarkCount() {
		return bookmarkCount;
	}

	public BigDecimal getHotScore() {
		return hotScore;
	}

	public boolean isPublished() {
		return published;
	}

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	public List<ContentCard> getCards() {
		return cards;
	}

	public Set<Tag> getTags() {
		return tags;
	}
}
