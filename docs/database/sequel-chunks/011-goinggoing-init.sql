-- GoingGoing Cloudtype DB init chunk 011/015
-- Sequel Ace에서 파일 번호 순서대로 전체 실행하세요.

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


UPDATE contents
SET external_area_code = '26',
    external_sigungu_code = '26500'
WHERE title = '광안리 해수욕장';


UPDATE contents
SET external_area_code = '51',
    external_sigungu_code = '51130'
WHERE title = '뮤지엄산';
