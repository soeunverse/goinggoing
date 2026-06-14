-- GoingGoing Cloudtype DB init chunk 009/015
-- Sequel Ace에서 파일 번호 순서대로 전체 실행하세요.

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
