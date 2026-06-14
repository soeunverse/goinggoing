-- GoingGoing Cloudtype DB init chunk 008/015
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
