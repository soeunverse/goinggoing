-- GoingGoing Cloudtype DB init chunk 004/015
-- Sequel Ace에서 파일 번호 순서대로 전체 실행하세요.

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
