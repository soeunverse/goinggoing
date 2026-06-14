-- GoingGoing Cloudtype DB init chunk 012/015
-- Sequel Ace에서 파일 번호 순서대로 전체 실행하세요.

INSERT INTO contents (
  region_id, theme_id, sub_theme_id, title, content_type, summary, description, address,
  latitude, longitude, thumbnail_url, source_type, external_content_id,
  view_count, bookmark_count, hot_score, is_published
)
SELECT
  region.id, theme.id, sub_theme.id, '북촌 한옥마을', 'ATTRACTION',
  '서울에서 전통 골목과 사진 스팟을 가볍게 묶기 좋은 대표 장소',
  '한옥 골목, 전시 공간, 카페를 함께 보기 좋은 서울 당일치기 컨텐츠입니다.',
  '서울 종로구 계동길 37', 37.5826000, 126.9830000,
  'https://images.unsplash.com/photo-1538485399081-7191377e8241',
  'ADMIN', 'seed-bukchon', 180, 45, 225.00, TRUE
FROM regions region
JOIN themes theme ON theme.name = '역사/문화'
JOIN sub_themes sub_theme ON sub_theme.theme_id = theme.id AND sub_theme.name = '사찰/고궁'
WHERE region.name = '서울'
ON DUPLICATE KEY UPDATE
  region_id = VALUES(region_id), theme_id = VALUES(theme_id), sub_theme_id = VALUES(sub_theme_id),
  title = VALUES(title), content_type = VALUES(content_type), summary = VALUES(summary),
  description = VALUES(description), address = VALUES(address), latitude = VALUES(latitude),
  longitude = VALUES(longitude), thumbnail_url = VALUES(thumbnail_url),
  view_count = VALUES(view_count), bookmark_count = VALUES(bookmark_count),
  hot_score = VALUES(hot_score), is_published = VALUES(is_published);


INSERT INTO contents (
  region_id, theme_id, sub_theme_id, title, content_type, summary, description, address,
  latitude, longitude, thumbnail_url, source_type, external_content_id,
  view_count, bookmark_count, hot_score, is_published
)
SELECT
  region.id, theme.id, sub_theme.id, '안목해변 카페거리', 'CAFE',
  '강릉 바다와 카페를 한 번에 잡는 1박 2일용 대표 컨텐츠',
  '커피, 해변 산책, 사진 스팟을 묶어 느슨하게 쉬기 좋은 컨텐츠입니다.',
  '강원 강릉시 창해로14번길', 37.7716000, 128.9483000,
  'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee',
  'ADMIN', 'seed-anmok', 140, 35, 175.00, TRUE
FROM regions region
JOIN themes theme ON theme.name = '자연/바다'
JOIN sub_themes sub_theme ON sub_theme.theme_id = theme.id AND sub_theme.name = '해변'
WHERE region.name = '강원'
ON DUPLICATE KEY UPDATE
  region_id = VALUES(region_id), theme_id = VALUES(theme_id), sub_theme_id = VALUES(sub_theme_id),
  title = VALUES(title), content_type = VALUES(content_type), summary = VALUES(summary),
  description = VALUES(description), address = VALUES(address), latitude = VALUES(latitude),
  longitude = VALUES(longitude), thumbnail_url = VALUES(thumbnail_url),
  view_count = VALUES(view_count), bookmark_count = VALUES(bookmark_count),
  hot_score = VALUES(hot_score), is_published = VALUES(is_published);


INSERT INTO contents (
  region_id, theme_id, sub_theme_id, title, content_type, summary, description, address,
  latitude, longitude, thumbnail_url, source_type, external_content_id,
  view_count, bookmark_count, hot_score, is_published
)
SELECT
  region.id, theme.id, sub_theme.id, '전주 한옥마을', 'ATTRACTION',
  '먹거리와 전통 골목을 함께 즐기는 전북 대표 당일치기 컨텐츠',
  '전통시장, 골목 산책, 로컬 간식을 묶기 좋은 컨텐츠입니다.',
  '전북 전주시 완산구 기린대로 99', 35.8151000, 127.1530000,
  'https://images.unsplash.com/photo-1548115184-bc6544d06a58',
  'ADMIN', 'seed-jeonju-hanok', 210, 55, 265.00, TRUE
FROM regions region
JOIN themes theme ON theme.name = '역사/문화'
JOIN sub_themes sub_theme ON sub_theme.theme_id = theme.id AND sub_theme.name = '전통시장'
WHERE region.name = '전북'
ON DUPLICATE KEY UPDATE
  region_id = VALUES(region_id), theme_id = VALUES(theme_id), sub_theme_id = VALUES(sub_theme_id),
  title = VALUES(title), content_type = VALUES(content_type), summary = VALUES(summary),
  description = VALUES(description), address = VALUES(address), latitude = VALUES(latitude),
  longitude = VALUES(longitude), thumbnail_url = VALUES(thumbnail_url),
  view_count = VALUES(view_count), bookmark_count = VALUES(bookmark_count),
  hot_score = VALUES(hot_score), is_published = VALUES(is_published);
