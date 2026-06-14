# Cloudtype Docker 배포 설정

## 배포 방식

- 배포 타입: Dockerfile
- Dockerfile 경로: `Dockerfile`
- 컨테이너 포트: `8080`
- Spring 실행 포트: `${PORT:8080}`

## 필수 환경변수

```env
PORT=8080
DB_URL=jdbc:mariadb://{HOST}:{PORT}/{DATABASE}
DB_USERNAME={DB_USER}
DB_PASSWORD={DB_PASSWORD}
JWT_SECRET={충분히_긴_랜덤_문자열}
KTO_SYNC_ENABLED=true
KTO_SERVICE_KEY={재발급한_공공데이터_인증키}
KTO_BASE_YM=202503
CORS_ALLOWED_ORIGINS={프론트_도메인}
```

## 배포 전 DB 작업

MariaDB 접속 후 순서대로 실행합니다.

```sql
-- 1. 테이블 생성
-- docs/database/goinggoing-mariadb.sql 실행

-- 2. MVP 데이터 입력
-- docs/database/seed-mvp.sql 실행
```

## 배포 후 확인

```text
GET /swagger-ui.html
POST /api/auth/login
GET /api/contents
POST /api/admin/sync/related-places
```

공공데이터 동기화가 정상이라면 서버 로그에 아래 로그가 출력됩니다.

```text
[KTO 요청]
[KTO 응답]
[DB 저장] 연관 관광지 저장
```
