-- GoingGoing Cloudtype DB init chunk 006/015
-- Sequel Ace에서 파일 번호 순서대로 전체 실행하세요.

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
