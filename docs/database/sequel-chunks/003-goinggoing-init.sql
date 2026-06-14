-- GoingGoing Cloudtype DB init chunk 003/015
-- Sequel Ace에서 파일 번호 순서대로 전체 실행하세요.

CREATE TABLE IF NOT EXISTS content_tags (
  content_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  PRIMARY KEY (content_id, tag_id),
  KEY idx_content_tags_tag_id (tag_id),
  CONSTRAINT fk_content_tags_content
    FOREIGN KEY (content_id) REFERENCES contents (id),
  CONSTRAINT fk_content_tags_tag
    FOREIGN KEY (tag_id) REFERENCES tags (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS user_preferences (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  preferred_trip_duration ENUM('DAY_TRIP', 'ONE_NIGHT_TWO_DAYS') NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_preferences_user_id (user_id),
  CONSTRAINT fk_user_preferences_user
    FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS user_preference_regions (
  user_preference_id BIGINT NOT NULL,
  region_id BIGINT NOT NULL,
  PRIMARY KEY (user_preference_id, region_id),
  KEY idx_user_preference_regions_region_id (region_id),
  CONSTRAINT fk_user_preference_regions_preference
    FOREIGN KEY (user_preference_id) REFERENCES user_preferences (id),
  CONSTRAINT fk_user_preference_regions_region
    FOREIGN KEY (region_id) REFERENCES regions (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS user_preference_themes (
  user_preference_id BIGINT NOT NULL,
  theme_id BIGINT NOT NULL,
  PRIMARY KEY (user_preference_id, theme_id),
  KEY idx_user_preference_themes_theme_id (theme_id),
  CONSTRAINT fk_user_preference_themes_preference
    FOREIGN KEY (user_preference_id) REFERENCES user_preferences (id),
  CONSTRAINT fk_user_preference_themes_theme
    FOREIGN KEY (theme_id) REFERENCES themes (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS user_preference_tags (
  user_preference_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  PRIMARY KEY (user_preference_id, tag_id),
  KEY idx_user_preference_tags_tag_id (tag_id),
  CONSTRAINT fk_user_preference_tags_preference
    FOREIGN KEY (user_preference_id) REFERENCES user_preferences (id),
  CONSTRAINT fk_user_preference_tags_tag
    FOREIGN KEY (tag_id) REFERENCES tags (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS bookmarks (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  content_id BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_bookmarks_user_content (user_id, content_id),
  KEY idx_bookmarks_user_id (user_id),
  KEY idx_bookmarks_content_id (content_id),
  CONSTRAINT fk_bookmarks_user
    FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_bookmarks_content
    FOREIGN KEY (content_id) REFERENCES contents (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
