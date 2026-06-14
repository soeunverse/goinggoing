-- GoingGoing Cloudtype DB init chunk 010/015
-- Sequel Ace에서 파일 번호 순서대로 전체 실행하세요.

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
