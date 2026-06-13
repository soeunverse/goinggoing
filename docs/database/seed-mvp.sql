-- GoingGoing MVP seed data.
-- docs/database/goinggoing-mariadb.sql 실행 후 전체 실행하세요.

USE goinggoing;

INSERT INTO regions (area_code, sigungu_code, name, full_name, display_order)
VALUES
  ('1', '0', '서울', '서울특별시', 1),
  ('2', '0', '인천', '인천광역시', 2),
  ('3', '0', '대전', '대전광역시', 3),
  ('4', '0', '대구', '대구광역시', 4),
  ('5', '0', '광주', '광주광역시', 5),
  ('6', '0', '부산', '부산광역시', 6),
  ('7', '0', '울산', '울산광역시', 7),
  ('8', '0', '세종', '세종특별자치시', 8),
  ('31', '0', '경기', '경기도', 9),
  ('32', '0', '강원', '강원특별자치도', 10),
  ('33', '0', '충북', '충청북도', 11),
  ('34', '0', '충남', '충청남도', 12),
  ('35', '0', '경북', '경상북도', 13),
  ('36', '0', '경남', '경상남도', 14),
  ('37', '0', '전북', '전북특별자치도', 15),
  ('38', '0', '전남', '전라남도', 16),
  ('39', '0', '제주', '제주특별자치도', 17)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  full_name = VALUES(full_name),
  display_order = VALUES(display_order);

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
