-- GoingGoing MariaDB schema setup.
-- Sequel Ace에서 이 파일을 실행할 때는 일부 줄만 선택하지 말고 전체 선택(Cmd+A) 후 Run All로 실행하세요.
-- 이 파일은 데이터베이스 생성, 데이터베이스 선택, 테이블 생성을 모두 포함합니다.

CREATE DATABASE IF NOT EXISTS goinggoing
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE goinggoing;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  nickname VARCHAR(50) NOT NULL,
  role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
  status ENUM('ACTIVE', 'SUSPENDED', 'DELETED') NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS refresh_tokens (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  token VARCHAR(512) NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  revoked_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_refresh_tokens_token (token),
  KEY idx_refresh_tokens_user_id (user_id),
  KEY idx_refresh_tokens_expires_at (expires_at),
  CONSTRAINT fk_refresh_tokens_user
    FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS regions (
  id BIGINT NOT NULL AUTO_INCREMENT,
  parent_id BIGINT NULL,
  area_code VARCHAR(20) NULL,
  sigungu_code VARCHAR(20) NULL,
  name VARCHAR(100) NOT NULL,
  full_name VARCHAR(200) NOT NULL,
  display_order INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_regions_area_sigungu (area_code, sigungu_code),
  KEY idx_regions_parent_id (parent_id),
  CONSTRAINT fk_regions_parent
    FOREIGN KEY (parent_id) REFERENCES regions (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS themes (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255) NULL,
  display_order INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_themes_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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

CREATE TABLE IF NOT EXISTS routes (
  id BIGINT NOT NULL AUTO_INCREMENT,
  content_id BIGINT NOT NULL,
  theme_id BIGINT NULL,
  title VARCHAR(200) NOT NULL,
  summary VARCHAR(500) NULL,
  trip_duration_type ENUM('DAY_TRIP', 'ONE_NIGHT_TWO_DAYS') NOT NULL,
  status ENUM('DRAFT', 'PUBLISHED', 'HIDDEN') NOT NULL DEFAULT 'DRAFT',
  total_distance_meters INT NULL,
  total_duration_minutes INT NULL,
  map_center_latitude DECIMAL(10, 7) NULL,
  map_center_longitude DECIMAL(10, 7) NULL,
  created_by VARCHAR(100) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  published_at TIMESTAMP NULL,
  PRIMARY KEY (id),
  KEY idx_routes_content_id (content_id),
  KEY idx_routes_theme_id (theme_id),
  KEY idx_routes_trip_duration (trip_duration_type),
  KEY idx_routes_status (status),
  CONSTRAINT fk_routes_content
    FOREIGN KEY (content_id) REFERENCES contents (id),
  CONSTRAINT fk_routes_theme
    FOREIGN KEY (theme_id) REFERENCES themes (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS route_places (
  id BIGINT NOT NULL AUTO_INCREMENT,
  route_id BIGINT NOT NULL,
  content_id BIGINT NULL,
  name VARCHAR(200) NOT NULL,
  place_type ENUM('ATTRACTION', 'RESTAURANT', 'CAFE', 'ACCOMMODATION', 'FESTIVAL', 'SHOPPING', 'ETC') NOT NULL,
  address VARCHAR(500) NULL,
  latitude DECIMAL(10, 7) NULL,
  longitude DECIMAL(10, 7) NULL,
  day_number INT NOT NULL DEFAULT 1,
  visit_order INT NOT NULL,
  estimated_stay_minutes INT NULL,
  move_minutes_from_previous INT NULL,
  recommendation_note VARCHAR(500) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_route_places_route_day_order (route_id, day_number, visit_order),
  KEY idx_route_places_route_id (route_id),
  KEY idx_route_places_content_id (content_id),
  CONSTRAINT fk_route_places_route
    FOREIGN KEY (route_id) REFERENCES routes (id),
  CONSTRAINT fk_route_places_content
    FOREIGN KEY (content_id) REFERENCES contents (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS related_places (
  id BIGINT NOT NULL AUTO_INCREMENT,
  base_content_id BIGINT NOT NULL,
  related_content_id BIGINT NULL,
  name VARCHAR(200) NOT NULL,
  place_type ENUM('ATTRACTION', 'RESTAURANT', 'CAFE', 'ACCOMMODATION', 'FESTIVAL', 'SHOPPING', 'ETC') NOT NULL,
  related_rank INT NOT NULL,
  relation_score DECIMAL(10, 2) NULL,
  address VARCHAR(500) NULL,
  latitude DECIMAL(10, 7) NULL,
  longitude DECIMAL(10, 7) NULL,
  source_period VARCHAR(20) NULL,
  raw_payload JSON NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_related_places_base_content_id (base_content_id),
  KEY idx_related_places_related_content_id (related_content_id),
  KEY idx_related_places_base_type_rank (base_content_id, place_type, related_rank),
  CONSTRAINT fk_related_places_base_content
    FOREIGN KEY (base_content_id) REFERENCES contents (id),
  CONSTRAINT fk_related_places_related_content
    FOREIGN KEY (related_content_id) REFERENCES contents (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS regional_demand_metrics (
  id BIGINT NOT NULL AUTO_INCREMENT,
  region_id BIGINT NOT NULL,
  metric_month VARCHAR(7) NOT NULL,
  service_demand_score DECIMAL(10, 2) NULL,
  cultural_resource_score DECIMAL(10, 2) NULL,
  navigation_search_score DECIMAL(10, 2) NULL,
  raw_payload JSON NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_regional_demand_region_month (region_id, metric_month),
  KEY idx_regional_demand_region_id (region_id),
  CONSTRAINT fk_regional_demand_region
    FOREIGN KEY (region_id) REFERENCES regions (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS search_logs (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NULL,
  keyword VARCHAR(200) NOT NULL,
  result_count INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_search_logs_user_id (user_id),
  KEY idx_search_logs_keyword (keyword),
  KEY idx_search_logs_created_at (created_at),
  CONSTRAINT fk_search_logs_user
    FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS external_sync_logs (
  id BIGINT NOT NULL AUTO_INCREMENT,
  source_type ENUM('KTO_TOUR_API', 'KTO_RELATED_ATTRACTION', 'KTO_REGIONAL_DEMAND', 'ADMIN') NOT NULL,
  endpoint VARCHAR(255) NOT NULL,
  status ENUM('SUCCESS', 'FAILED', 'PARTIAL') NOT NULL,
  requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at TIMESTAMP NULL,
  imported_count INT NOT NULL DEFAULT 0,
  failed_count INT NOT NULL DEFAULT 0,
  message TEXT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'goinggoing'
ORDER BY table_name;
