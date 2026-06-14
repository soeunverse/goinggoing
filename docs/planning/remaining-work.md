# 남은 작업 정리

## 현재 구현된 API

- 인증: 회원가입, 로그인, 로그아웃, 토큰 갱신
- 사용자: 내 정보 조회, 내 정보 수정, 회원 탈퇴
- 온보딩 취향: 조회, 저장
- 카테고리: 지역, 테마, 하위 카테고리, 태그 조회
- 컨텐츠 조회: 목록, 상세, HOT 조회
- 컨텐츠 관리: 관리자 생성, 수정, 삭제
- 검색: 키워드 검색, 필터 검색, 인기 검색어, 최근 검색어
- 찜: 목록, 추가, 단건 삭제, 일괄 삭제
- 추천: 피드 추천, 연관 컨텐츠 추천, 룰렛 후보 추천
- 루트: 루트 생성, 상세 조회, 지역별 조회
- 외부 데이터 동기화: 관리자용 동기화 실행 로그 구조

## MVP 기준으로 남은 핵심 작업

- 한국관광공사 API 실제 HTTP client 구현
- 외부 API 응답을 내부 `contents`, `related_places`, `regional_demand_metrics`로 가공 저장
- 관리자 계정 생성 방식 확정
- 배포 DB 스키마 생성 자동화 또는 운영 절차 확정
- Cloudtype 환경변수 세팅
- 배포 후 Swagger 수동 테스트
- React Native 앱에서 API base URL 연결

## MVP 이후 개선 작업

- Spring Security filter 기반 JWT 인증 전환
- Refresh token rotation 정책 고도화
- Flyway 또는 Liquibase로 DB migration 관리
- 운영/로컬 profile 분리
- 컨텐츠 이미지 저장소 정책 정리
- 추천 알고리즘 고도화
- 관리자 전용 API 접근 제어 강화
- 배포 로그/모니터링 체계 추가
