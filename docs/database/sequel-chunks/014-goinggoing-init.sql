-- GoingGoing Cloudtype DB init chunk 014/015
-- Sequel Ace에서 파일 번호 순서대로 전체 실행하세요.

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
