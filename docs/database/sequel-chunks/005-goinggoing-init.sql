-- GoingGoing Cloudtype DB init chunk 005/015
-- Sequel Ace에서 파일 번호 순서대로 전체 실행하세요.

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



-- GoingGoing MVP seed data.
-- docs/database/goinggoing-mariadb.sql 실행 후 전체 실행하세요.

USE goinggoing;


INSERT INTO regions (area_code, sigungu_code, name, full_name, display_order)
VALUES
  ('1', '0', '서울', '서울특별시', 1),
  ('2', '0', '인천', '인천광역시', 2),
  ('3', '0', '대전', '대전광역시', 3),
  ('4', '0', '대구', '대구광역시', 4),
  ('5', '0', '광주', '광주광역시', 5),
  ('6', '0', '부산', '부산광역시', 6),
  ('7', '0', '울산', '울산광역시', 7),
  ('8', '0', '세종', '세종특별자치시', 8),
  ('31', '0', '경기', '경기도', 9),
  ('32', '0', '강원', '강원특별자치도', 10),
  ('33', '0', '충북', '충청북도', 11),
  ('34', '0', '충남', '충청남도', 12),
  ('35', '0', '경북', '경상북도', 13),
  ('36', '0', '경남', '경상남도', 14),
  ('37', '0', '전북', '전북특별자치도', 15),
  ('38', '0', '전남', '전라남도', 16),
  ('39', '0', '제주', '제주특별자치도', 17)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  full_name = VALUES(full_name),
  display_order = VALUES(display_order);
