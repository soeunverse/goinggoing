# Cloudtype 배포 정리

## 배포 대상

- Backend: Spring Boot 3.5, Java 21
- Database: MariaDB
- Deploy source: Git repository
- Frontend: React Native

## 1. MariaDB 준비

Cloudtype에서 MariaDB를 같이 사용할 때는 프로젝트에 MariaDB 서비스를 먼저 만들고, 아래 정보를 확인합니다.

- Host
- Port
- Database name
- Username
- Password

예시 JDBC URL:

```text
jdbc:mariadb://{DB_HOST}:{DB_PORT}/{DB_NAME}
```

로컬과 다르게 Cloudtype에서는 `localhost`를 사용하면 안 됩니다. 백엔드 컨테이너 기준의 localhost는 백엔드 자기 자신이기 때문입니다.

## 2. Cloudtype 환경변수

백엔드 서비스 환경변수에 아래 값을 등록합니다.

```text
DB_URL=jdbc:mariadb://{DB_HOST}:{DB_PORT}/{DB_NAME}
DB_USERNAME={DB_USERNAME}
DB_PASSWORD={DB_PASSWORD}
JWT_SECRET={최소 32자 이상 랜덤 문자열}
JWT_ZONE_ID=Asia/Seoul
ACCESS_TOKEN_EXPIRES_IN_SECONDS=3600
REFRESH_TOKEN_EXPIRES_IN_DAYS=14
CORS_ALLOWED_ORIGINS={프론트에서 호출하는 Origin}
KTO_SYNC_ENABLED=true
KTO_SERVICE_KEY={한국관광공사 API 인증키}
KTO_DEMAND_ROWS=100
KTO_RELATED_PLACE_ROWS=20
```

프로젝트에는 Git 배포용 `src/main/resources/application.yml`이 포함되어 있고, 위 환경변수를 읽어 DB와 CORS를 설정합니다. 로컬의 `application.properties`는 `.gitignore` 대상이므로 운영 비밀번호를 커밋하지 않습니다.

React Native 앱은 네이티브 실행 환경에서는 브라우저 CORS 영향을 직접 받지 않지만, Expo Web, 웹뷰, 로컬 웹 테스트를 대비해 CORS를 열어둡니다.

로컬 개발 예시:

```text
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173,http://localhost:8081,http://localhost:19006
```

배포 후 웹 프론트가 있다면 예시:

```text
CORS_ALLOWED_ORIGINS=https://goinggoing-front.example.com
```

## 3. Git 레포 연결 배포

1. GitHub에 백엔드 레포 push
2. Cloudtype에서 새 애플리케이션 생성
3. 배포 방식으로 Git repository 선택
4. Repository와 branch 선택
5. Runtime은 Java 또는 Gradle 기반으로 설정
6. Build command:

```bash
./gradlew clean build
```

7. Start command:

```bash
java -jar build/libs/goinggoing-0.0.1-SNAPSHOT.jar
```

8. Port:

```text
8080
```

## 4. DB 테이블 생성

배포 DB에 테이블이 없으면 Sequel Ace 또는 Cloudtype DB 콘솔에서 아래 순서로 실행합니다.

1. `docs/database/goinggoing-mariadb.sql`
2. `docs/database/sequel-ace-seed-data.sql`

운영 배포에서는 seed 데이터는 테스트용이므로 필요할 때만 실행합니다.

## 5. 배포 후 확인

Swagger:

```text
https://{cloudtype-backend-domain}/swagger-ui.html
```

Health check 대용 확인:

```text
GET /api/categories/regions
GET /api/contents
GET /api/recommendations/feed
```

IntelliJ 또는 Cloudtype 로그에서 확인할 로그:

```text
[API 요청] GET /api/contents
[API 응답] GET /api/contents status=200 time=...
[DB 저장] ...
[DB 수정] ...
[DB 삭제] ...
```

## 6. 주의사항

- `spring.jpa.hibernate.ddl-auto=none`이므로 애플리케이션 실행만으로 테이블이 생성되지 않습니다.
- DB 스키마는 SQL 파일로 먼저 생성해야 합니다.
- JWT secret은 로컬 기본값을 운영에서 그대로 쓰면 안 됩니다.
- DB 비밀번호는 Git에 올리지 말고 Cloudtype 환경변수로만 관리합니다.
