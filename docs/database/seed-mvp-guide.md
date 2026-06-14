# MVP 테스트 데이터 정리

`docs/database/seed-mvp.sql`은 Sequel Ace에서 직접 실행할 수 있는 MVP 테스트용 기본 데이터입니다.

## 실행 순서

1. `docs/database/goinggoing-mariadb.sql` 전체 실행
2. `docs/database/seed-mvp.sql` 전체 실행
3. Swagger에서 조회/검색/추천/찜/루트 API 테스트

## 포함 데이터

- 지역: 전국 17개 시/도
- 테마: 맛집, 자연/바다, 도시/핫플, 역사/문화, 힐링, 쇼핑
- 하위 카테고리: 테마별 4개씩 총 24개
- 태그: 성심당, 광안리, 빵지순례, 바다, 야경, 카페, 디저트 등 24개
- 컨텐츠: 성심당, 광안리 해수욕장, 북촌 한옥마을, 안목해변 카페거리, 전주 한옥마을, 성산일출봉
- 카드뉴스: 컨텐츠별 대표 설명 카드
- 추천 루트: 성심당 당일치기 루트, 광안리 1박 2일 루트

## Swagger 확인용 예시

- 컨텐츠 목록: `GET /api/contents`
- HOT 컨텐츠: `GET /api/contents/hot`
- 키워드 검색: `GET /api/search?q=성심당`
- 필터 검색: `GET /api/search/filter?regionId=3&themeId=1`
- 피드 추천: `GET /api/recommendations/feed`
- 룰렛 후보: `GET /api/recommendations/roulette?themeId=2`
- 루트 생성: `POST /api/routes/generate`
