-- GoingGoing Cloudtype DB init chunk 013/015
-- Sequel Ace에서 파일 번호 순서대로 전체 실행하세요.

INSERT INTO contents (
  region_id, theme_id, sub_theme_id, title, content_type, summary, description, address,
  latitude, longitude, thumbnail_url, source_type, external_content_id,
  view_count, bookmark_count, hot_score, is_published
)
SELECT
  region.id, theme.id, sub_theme.id, '성산일출봉', 'ATTRACTION',
  '제주 1박 2일에서 풍경과 산책을 잡기 좋은 대표 자연 컨텐츠',
  '전망, 해안 드라이브, 가벼운 산책을 함께 묶기 좋은 컨텐츠입니다.',
  '제주 서귀포시 성산읍 성산리 1', 33.4588000, 126.9425000,
  'https://images.unsplash.com/photo-1507525428034-b723cf961d3e',
  'ADMIN', 'seed-seongsan', 260, 70, 330.00, TRUE
FROM regions region
JOIN themes theme ON theme.name = '자연/바다'
JOIN sub_themes sub_theme ON sub_theme.theme_id = theme.id AND sub_theme.name = '전망'
WHERE region.name = '제주'
ON DUPLICATE KEY UPDATE
  region_id = VALUES(region_id), theme_id = VALUES(theme_id), sub_theme_id = VALUES(sub_theme_id),
  title = VALUES(title), content_type = VALUES(content_type), summary = VALUES(summary),
  description = VALUES(description), address = VALUES(address), latitude = VALUES(latitude),
  longitude = VALUES(longitude), thumbnail_url = VALUES(thumbnail_url),
  view_count = VALUES(view_count), bookmark_count = VALUES(bookmark_count),
  hot_score = VALUES(hot_score), is_published = VALUES(is_published);


INSERT INTO content_cards (content_id, title, body, image_url, display_order)
SELECT id, '골목 산책', '한옥 골목과 작은 전시 공간을 천천히 둘러보기 좋습니다.', thumbnail_url, 1
FROM contents WHERE external_content_id = 'seed-bukchon'
ON DUPLICATE KEY UPDATE title = VALUES(title), body = VALUES(body), image_url = VALUES(image_url);


INSERT INTO content_cards (content_id, title, body, image_url, display_order)
SELECT id, '바다 카페', '바다를 보며 커피를 마시고 해변 산책까지 이어가기 좋습니다.', thumbnail_url, 1
FROM contents WHERE external_content_id = 'seed-anmok'
ON DUPLICATE KEY UPDATE title = VALUES(title), body = VALUES(body), image_url = VALUES(image_url);


INSERT INTO content_cards (content_id, title, body, image_url, display_order)
SELECT id, '전주 먹거리', '한옥 골목과 시장 먹거리를 함께 묶으면 동선이 단순해집니다.', thumbnail_url, 1
FROM contents WHERE external_content_id = 'seed-jeonju-hanok'
ON DUPLICATE KEY UPDATE title = VALUES(title), body = VALUES(body), image_url = VALUES(image_url);


INSERT INTO content_cards (content_id, title, body, image_url, display_order)
SELECT id, '제주 전망', '성산 일대 전망과 해안 드라이브를 같이 잡기 좋습니다.', thumbnail_url, 1
FROM contents WHERE external_content_id = 'seed-seongsan'
ON DUPLICATE KEY UPDATE title = VALUES(title), body = VALUES(body), image_url = VALUES(image_url);


INSERT INTO content_tags (content_id, tag_id)
SELECT content.id, tag.id
FROM contents content
JOIN tags tag ON tag.name IN ('포토스팟', '골목', '박물관')
WHERE content.external_content_id = 'seed-bukchon'
ON DUPLICATE KEY UPDATE content_id = VALUES(content_id);


INSERT INTO content_tags (content_id, tag_id)
SELECT content.id, tag.id
FROM contents content
JOIN tags tag ON tag.name IN ('바다', '카페', '산책')
WHERE content.external_content_id = 'seed-anmok'
ON DUPLICATE KEY UPDATE content_id = VALUES(content_id);


INSERT INTO content_tags (content_id, tag_id)
SELECT content.id, tag.id
FROM contents content
JOIN tags tag ON tag.name IN ('전통시장', '로컬맛집', '골목')
WHERE content.external_content_id = 'seed-jeonju-hanok'
ON DUPLICATE KEY UPDATE content_id = VALUES(content_id);


INSERT INTO content_tags (content_id, tag_id)
SELECT content.id, tag.id
FROM contents content
JOIN tags tag ON tag.name IN ('전망', '드라이브', '힐링')
WHERE content.external_content_id = 'seed-seongsan'
ON DUPLICATE KEY UPDATE content_id = VALUES(content_id);
