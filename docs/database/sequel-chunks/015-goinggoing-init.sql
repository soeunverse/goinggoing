-- GoingGoing Cloudtype DB init chunk 015/015
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
