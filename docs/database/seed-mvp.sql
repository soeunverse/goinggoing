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

INSERT INTO themes (name, description, display_order)
VALUES
  ('맛집', '먹으러 떠나는 당일치기 여행', 1),
  ('자연/바다', '바다, 숲, 강을 즐기는 여행', 2),
  ('도시/핫플', '도시의 유명 장소와 포토스팟', 3),
  ('역사/문화', '지역의 문화와 이야기를 만나는 여행', 4),
  ('힐링', '느슨하게 쉬어가는 여행', 5),
  ('쇼핑', '시장, 편집숍, 로컬 브랜드 탐방', 6)
ON DUPLICATE KEY UPDATE
  description = VALUES(description),
  display_order = VALUES(display_order);

INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '빵지순례', '유명 빵집과 디저트 코스', 1 FROM themes WHERE name = '맛집'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);
INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '로컬맛집', '지역 주민이 찾는 맛집', 2 FROM themes WHERE name = '맛집'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);
INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '카페', '커피와 분위기를 즐기는 장소', 3 FROM themes WHERE name = '맛집'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);
INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '디저트', '달콤한 간식과 디저트', 4 FROM themes WHERE name = '맛집'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);

INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '해변', '바다와 해수욕장', 1 FROM themes WHERE name = '자연/바다'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);
INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '산책', '가볍게 걷기 좋은 장소', 2 FROM themes WHERE name = '자연/바다'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);
INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '전망', '풍경을 내려다보는 장소', 3 FROM themes WHERE name = '자연/바다'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);
INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '섬', '섬 여행과 해안 코스', 4 FROM themes WHERE name = '자연/바다'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);

INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '야경', '밤에 더 좋은 도시 풍경', 1 FROM themes WHERE name = '도시/핫플'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);
INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '전시', '전시와 문화 공간', 2 FROM themes WHERE name = '도시/핫플'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);
INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '포토스팟', '사진 찍기 좋은 장소', 3 FROM themes WHERE name = '도시/핫플'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);
INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '거리/골목', '걷기 좋은 도시 골목', 4 FROM themes WHERE name = '도시/핫플'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);

INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '박물관', '박물관과 기록 공간', 1 FROM themes WHERE name = '역사/문화'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);
INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '전통시장', '시장과 지역 생활 문화', 2 FROM themes WHERE name = '역사/문화'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);
INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '사찰/고궁', '전통 건축과 역사 장소', 3 FROM themes WHERE name = '역사/문화'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);
INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '축제', '지역 축제와 행사', 4 FROM themes WHERE name = '역사/문화'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);

INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '온천', '따뜻하게 쉬는 장소', 1 FROM themes WHERE name = '힐링'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);
INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '숲', '조용한 숲과 자연', 2 FROM themes WHERE name = '힐링'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);
INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '조용한동네', '여유롭게 걷는 동네', 3 FROM themes WHERE name = '힐링'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);
INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '드라이브', '차로 가볍게 떠나는 코스', 4 FROM themes WHERE name = '힐링'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);

INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '편집숍', '취향 있는 편집숍', 1 FROM themes WHERE name = '쇼핑'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);
INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '로컬브랜드', '지역 기반 브랜드', 2 FROM themes WHERE name = '쇼핑'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);
INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '시장', '먹거리와 살거리가 있는 시장', 3 FROM themes WHERE name = '쇼핑'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);
INSERT INTO sub_themes (theme_id, name, description, display_order)
SELECT id, '기념품', '여행 기념품과 선물', 4 FROM themes WHERE name = '쇼핑'
ON DUPLICATE KEY UPDATE description = VALUES(description), display_order = VALUES(display_order);

INSERT INTO tags (name, display_order)
VALUES
  ('성심당', 1),
  ('광안리', 2),
  ('빵지순례', 3),
  ('바다', 4),
  ('야경', 5),
  ('카페', 6),
  ('디저트', 7),
  ('전통시장', 8),
  ('포토스팟', 9),
  ('로컬맛집', 10),
  ('산책', 11),
  ('전시', 12),
  ('드라이브', 13),
  ('힐링', 14),
  ('온천', 15),
  ('숲', 16),
  ('축제', 17),
  ('박물관', 18),
  ('편집숍', 19),
  ('기념품', 20),
  ('섬', 21),
  ('전망', 22),
  ('골목', 23),
  ('로컬브랜드', 24)
ON DUPLICATE KEY UPDATE
  display_order = VALUES(display_order);

INSERT INTO contents (
  region_id,
  theme_id,
  sub_theme_id,
  title,
  content_type,
  summary,
  description,
  address,
  latitude,
  longitude,
  thumbnail_url,
  source_type,
  external_content_id,
  view_count,
  bookmark_count,
  hot_score,
  is_published
)
SELECT
  region.id,
  theme.id,
  sub_theme.id,
  '성심당',
  'RESTAURANT',
  '대전 당일치기의 시작점으로 잡기 좋은 대표 빵집',
  '대전역과 원도심 동선에 붙이기 좋은 빵지순례 컨텐츠입니다.',
  '대전 중구 대종로480번길 15',
  36.3275000,
  127.4272000,
  'https://images.unsplash.com/photo-1509440159596-0249088772ff',
  'ADMIN',
  'seed-seongsimdang',
  120,
  30,
  150.00,
  TRUE
FROM regions region
JOIN themes theme ON theme.name = '맛집'
JOIN sub_themes sub_theme ON sub_theme.theme_id = theme.id AND sub_theme.name = '빵지순례'
WHERE region.name = '대전'
ON DUPLICATE KEY UPDATE
  region_id = VALUES(region_id),
  theme_id = VALUES(theme_id),
  sub_theme_id = VALUES(sub_theme_id),
  title = VALUES(title),
  content_type = VALUES(content_type),
  summary = VALUES(summary),
  description = VALUES(description),
  address = VALUES(address),
  latitude = VALUES(latitude),
  longitude = VALUES(longitude),
  thumbnail_url = VALUES(thumbnail_url),
  view_count = VALUES(view_count),
  bookmark_count = VALUES(bookmark_count),
  hot_score = VALUES(hot_score),
  is_published = VALUES(is_published);

INSERT INTO contents (
  region_id,
  theme_id,
  sub_theme_id,
  title,
  content_type,
  summary,
  description,
  address,
  latitude,
  longitude,
  thumbnail_url,
  source_type,
  external_content_id,
  view_count,
  bookmark_count,
  hot_score,
  is_published
)
SELECT
  region.id,
  theme.id,
  sub_theme.id,
  '광안리 해수욕장',
  'ATTRACTION',
  '부산 1박 2일의 밤 산책과 바다 뷰를 잡기 좋은 해변',
  '광안대교 야경, 카페, 해변 산책을 한 번에 묶기 좋은 대표 컨텐츠입니다.',
  '부산 수영구 광안해변로 219',
  35.1532000,
  129.1186000,
  'https://images.unsplash.com/photo-1507525428034-b723cf961d3e',
  'ADMIN',
  'seed-gwangalli',
  300,
  80,
  380.00,
  TRUE
FROM regions region
JOIN themes theme ON theme.name = '자연/바다'
JOIN sub_themes sub_theme ON sub_theme.theme_id = theme.id AND sub_theme.name = '해변'
WHERE region.name = '부산'
ON DUPLICATE KEY UPDATE
  region_id = VALUES(region_id),
  theme_id = VALUES(theme_id),
  sub_theme_id = VALUES(sub_theme_id),
  title = VALUES(title),
  content_type = VALUES(content_type),
  summary = VALUES(summary),
  description = VALUES(description),
  address = VALUES(address),
  latitude = VALUES(latitude),
  longitude = VALUES(longitude),
  thumbnail_url = VALUES(thumbnail_url),
  view_count = VALUES(view_count),
  bookmark_count = VALUES(bookmark_count),
  hot_score = VALUES(hot_score),
  is_published = VALUES(is_published);

INSERT INTO content_cards (content_id, title, body, image_url, display_order)
SELECT id, '대표 메뉴', '튀김소보로와 부추빵을 중심으로 가볍게 먹고 이동하기 좋습니다.', thumbnail_url, 1
FROM contents
WHERE external_content_id = 'seed-seongsimdang'
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  body = VALUES(body),
  image_url = VALUES(image_url);

INSERT INTO content_cards (content_id, title, body, image_url, display_order)
SELECT id, '추천 동선', '대전역, 중앙로, 성심당을 묶으면 당일치기 동선이 단순해집니다.', thumbnail_url, 2
FROM contents
WHERE external_content_id = 'seed-seongsimdang'
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  body = VALUES(body),
  image_url = VALUES(image_url);

INSERT INTO content_cards (content_id, title, body, image_url, display_order)
SELECT id, '밤 산책', '광안대교 야경을 보며 해변을 따라 걷기 좋습니다.', thumbnail_url, 1
FROM contents
WHERE external_content_id = 'seed-gwangalli'
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  body = VALUES(body),
  image_url = VALUES(image_url);

INSERT INTO content_cards (content_id, title, body, image_url, display_order)
SELECT id, '주변 코스', '해변 카페와 민락수변공원을 함께 묶기 좋습니다.', thumbnail_url, 2
FROM contents
WHERE external_content_id = 'seed-gwangalli'
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  body = VALUES(body),
  image_url = VALUES(image_url);

INSERT INTO content_tags (content_id, tag_id)
SELECT content.id, tag.id
FROM contents content
JOIN tags tag ON tag.name IN ('성심당', '빵지순례', '디저트')
WHERE content.external_content_id = 'seed-seongsimdang'
ON DUPLICATE KEY UPDATE
  content_id = VALUES(content_id);

INSERT INTO content_tags (content_id, tag_id)
SELECT content.id, tag.id
FROM contents content
JOIN tags tag ON tag.name IN ('광안리', '바다', '야경', '산책')
WHERE content.external_content_id = 'seed-gwangalli'
ON DUPLICATE KEY UPDATE
  content_id = VALUES(content_id);

INSERT INTO routes (
  content_id,
  theme_id,
  title,
  summary,
  trip_duration_type,
  status,
  total_distance_meters,
  total_duration_minutes,
  map_center_latitude,
  map_center_longitude,
  created_by,
  published_at
)
SELECT
  content.id,
  content.theme_id,
  '성심당 당일치기 루트',
  '대전 원도심을 가볍게 도는 당일치기 루트',
  'DAY_TRIP',
  'PUBLISHED',
  3200,
  180,
  36.3275000,
  127.4272000,
  'seed',
  CURRENT_TIMESTAMP
FROM contents content
WHERE content.external_content_id = 'seed-seongsimdang'
  AND NOT EXISTS (
    SELECT 1
    FROM routes existing_route
    WHERE existing_route.content_id = content.id
      AND existing_route.trip_duration_type = 'DAY_TRIP'
      AND existing_route.title = '성심당 당일치기 루트'
  );

INSERT INTO routes (
  content_id,
  theme_id,
  title,
  summary,
  trip_duration_type,
  status,
  total_distance_meters,
  total_duration_minutes,
  map_center_latitude,
  map_center_longitude,
  created_by,
  published_at
)
SELECT
  content.id,
  content.theme_id,
  '광안리 1박 2일 루트',
  '바다와 야경을 천천히 즐기는 부산 1박 2일 루트',
  'ONE_NIGHT_TWO_DAYS',
  'PUBLISHED',
  8500,
  420,
  35.1532000,
  129.1186000,
  'seed',
  CURRENT_TIMESTAMP
FROM contents content
WHERE content.external_content_id = 'seed-gwangalli'
  AND NOT EXISTS (
    SELECT 1
    FROM routes existing_route
    WHERE existing_route.content_id = content.id
      AND existing_route.trip_duration_type = 'ONE_NIGHT_TWO_DAYS'
      AND existing_route.title = '광안리 1박 2일 루트'
  );

INSERT INTO route_places (
  route_id,
  content_id,
  name,
  place_type,
  address,
  latitude,
  longitude,
  day_number,
  visit_order,
  estimated_stay_minutes,
  move_minutes_from_previous,
  recommendation_note
)
SELECT
  route.id,
  content.id,
  '성심당 본점',
  'RESTAURANT',
  '대전 중구 대종로480번길 15',
  36.3275000,
  127.4272000,
  1,
  1,
  60,
  NULL,
  '대표 빵집에서 여행 시작'
FROM routes route
JOIN contents content ON content.id = route.content_id
WHERE route.title = '성심당 당일치기 루트'
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  recommendation_note = VALUES(recommendation_note);

INSERT INTO route_places (
  route_id,
  content_id,
  name,
  place_type,
  address,
  latitude,
  longitude,
  day_number,
  visit_order,
  estimated_stay_minutes,
  move_minutes_from_previous,
  recommendation_note
)
SELECT
  route.id,
  NULL,
  '대전 중앙로 산책',
  'ATTRACTION',
  '대전 중구 중앙로',
  36.3270000,
  127.4250000,
  1,
  2,
  90,
  10,
  '성심당 근처 원도심을 가볍게 걷기'
FROM routes route
WHERE route.title = '성심당 당일치기 루트'
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  recommendation_note = VALUES(recommendation_note);

INSERT INTO route_places (
  route_id,
  content_id,
  name,
  place_type,
  address,
  latitude,
  longitude,
  day_number,
  visit_order,
  estimated_stay_minutes,
  move_minutes_from_previous,
  recommendation_note
)
SELECT
  route.id,
  content.id,
  '광안리 해수욕장',
  'ATTRACTION',
  '부산 수영구 광안해변로 219',
  35.1532000,
  129.1186000,
  1,
  1,
  120,
  NULL,
  '해변과 광안대교 야경을 함께 보기'
FROM routes route
JOIN contents content ON content.id = route.content_id
WHERE route.title = '광안리 1박 2일 루트'
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  recommendation_note = VALUES(recommendation_note);

INSERT INTO route_places (
  route_id,
  content_id,
  name,
  place_type,
  address,
  latitude,
  longitude,
  day_number,
  visit_order,
  estimated_stay_minutes,
  move_minutes_from_previous,
  recommendation_note
)
SELECT
  route.id,
  NULL,
  '민락수변공원',
  'ATTRACTION',
  '부산 수영구 민락동',
  35.1547000,
  129.1276000,
  2,
  1,
  90,
  15,
  '다음 날 바다를 보며 천천히 산책'
FROM routes route
WHERE route.title = '광안리 1박 2일 루트'
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  recommendation_note = VALUES(recommendation_note);
