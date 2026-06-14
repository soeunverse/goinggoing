# 한국관광공사 API 동기화 정리

## 사용 API

### 지역별 관광 자원 수요

- 데이터명: 한국관광공사_지역별 관광 자원 수요
- Base URL: `https://apis.data.go.kr/B551011/AreaTarResDemService`
- 기능:
  - `/areaTarSvcDemList`
  - `/areaCulResDemList`
- 저장 테이블: `regional_demand_metrics`

### 관광지별 연관 관광지 정보

- 데이터명: 한국관광공사_관광지별 연관 관광지 정보
- Base URL: `https://apis.data.go.kr/B551011/TarRlteTarService1`
- 기능:
  - `/searchKeyword1`
- 저장 테이블: `related_places`

## 제외 API

### 관광지 집중률 방문자 추이 예측 정보

- Base URL: `https://apis.data.go.kr/B551011/TatsCnctrRateService`
- MVP에서는 제외
- 추후 혼잡도/방문 타이밍 추천 기능이 필요할 때 추가

## 환경변수

```text
KTO_SYNC_ENABLED=true
KTO_SERVICE_KEY={한국관광공사 인증키}
KTO_DEMAND_ROWS=100
KTO_RELATED_PLACE_ROWS=20
```

## 동기화 실행

관리자 access token을 Swagger Authorize에 넣고 아래 API를 실행합니다.

```text
POST /api/admin/sync/regional-demand
POST /api/admin/sync/related-places
GET /api/admin/sync/logs
```

## 로그 확인

```text
[DB 저장] 지역수요 저장 regionId=3 regionName=대전 endpoint=REGIONAL_SERVICE_DEMAND count=...
[DB 저장] 연관 관광지 저장 baseContentId=1 keyword=성심당 count=...
[DB 저장] 외부 동기화 로그 저장 syncLogId=... sourceType=...
```

## 주의사항

- 인증키는 코드에 직접 넣지 않고 `KTO_SERVICE_KEY`로만 주입합니다.
- 채팅이나 문서에 노출된 인증키는 운영 전 재발급을 권장합니다.
- 실제 응답 필드명은 공공데이터 참고문서 zip 기준으로 최종 검증해야 합니다.
