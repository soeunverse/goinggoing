-- GoingGoing Cloudtype DB init chunk 007/015
-- Sequel Ace에서 파일 번호 순서대로 전체 실행하세요.

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
