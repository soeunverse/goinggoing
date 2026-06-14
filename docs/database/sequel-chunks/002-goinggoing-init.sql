-- GoingGoing Cloudtype DB init chunk 002/015
-- Sequel Ace에서 파일 번호 순서대로 전체 실행하세요.

CREATE TABLE IF NOT EXISTS sub_themes (
  id BIGINT NOT NULL AUTO_INCREMENT,
  theme_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255) NULL,
  display_order INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sub_themes_theme_name (theme_id, name),
  CONSTRAINT fk_sub_themes_theme
    FOREIGN KEY (theme_id) REFERENCES themes (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS tags (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  display_order INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_tags_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS contents (
  id BIGINT NOT NULL AUTO_INCREMENT,
  region_id BIGINT NOT NULL,
  theme_id BIGINT NULL,
  sub_theme_id BIGINT NULL,
  title VARCHAR(200) NOT NULL,
  content_type ENUM('ATTRACTION', 'RESTAURANT', 'CAFE', 'ACCOMMODATION', 'FESTIVAL', 'SHOPPING', 'ETC') NOT NULL,
  summary VARCHAR(500) NULL,
  description TEXT NULL,
  address VARCHAR(500) NULL,
  latitude DECIMAL(10, 7) NULL,
  longitude DECIMAL(10, 7) NULL,
  thumbnail_url VARCHAR(1000) NULL,
  source_type ENUM('KTO_TOUR_API', 'KTO_RELATED_ATTRACTION', 'KTO_REGIONAL_DEMAND', 'ADMIN') NOT NULL,
  external_content_id VARCHAR(100) NULL,
  external_content_type_id VARCHAR(50) NULL,
  external_area_code VARCHAR(50) NULL,
  external_sigungu_code VARCHAR(50) NULL,
  view_count BIGINT NOT NULL DEFAULT 0,
  bookmark_count BIGINT NOT NULL DEFAULT 0,
  hot_score DECIMAL(10, 2) NOT NULL DEFAULT 0,
  is_published BOOLEAN NOT NULL DEFAULT FALSE,
  synced_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_contents_source_external (source_type, external_content_id),
  KEY idx_contents_region_id (region_id),
  KEY idx_contents_theme_id (theme_id),
  KEY idx_contents_sub_theme_id (sub_theme_id),
  KEY idx_contents_type (content_type),
  KEY idx_contents_hot_score (hot_score),
  KEY idx_contents_published (is_published),
  CONSTRAINT fk_contents_region
    FOREIGN KEY (region_id) REFERENCES regions (id),
  CONSTRAINT fk_contents_theme
    FOREIGN KEY (theme_id) REFERENCES themes (id),
  CONSTRAINT fk_contents_sub_theme
    FOREIGN KEY (sub_theme_id) REFERENCES sub_themes (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS content_cards (
  id BIGINT NOT NULL AUTO_INCREMENT,
  content_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  body TEXT NOT NULL,
  image_url VARCHAR(1000) NULL,
  display_order INT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_content_cards_content_order (content_id, display_order),
  KEY idx_content_cards_content_id (content_id),
  CONSTRAINT fk_content_cards_content
    FOREIGN KEY (content_id) REFERENCES contents (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
