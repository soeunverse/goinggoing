# User API Manual Test

## 1. Local MariaDB

1. Start MariaDB.
   ```bash
   brew services restart mariadb
   ```

2. In Sequel Ace, connect with an admin account and run:
   - `docs/database/local-mariadb-user.sql`
   - `docs/database/goinggoing-mariadb.sql`

3. Verify the application account.
   ```bash
   mariadb --socket=/tmp/mysql.sock -u goinggoing_user -pgoinggoing_password -D goinggoing -e "SHOW TABLES;"
   ```

## 2. Run Server

```bash
./gradlew bootRun
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## 3. Auth Flow

Signup:

```bash
curl -i -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "nickname": "즉흥여행자"
  }'
```

Login:

```bash
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

Copy `data.accessToken` and `data.refreshToken`.

## 4. Protected User APIs

My profile:

```bash
curl -i http://localhost:8080/api/users/me \
  -H "Authorization: Bearer {accessToken}"
```

Update profile:

```bash
curl -i -X PATCH http://localhost:8080/api/users/me \
  -H "Authorization: Bearer {accessToken}" \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "새닉네임"
  }'
```

Save onboarding preference:

```bash
curl -i -X PUT http://localhost:8080/api/users/me/preferences \
  -H "Authorization: Bearer {accessToken}" \
  -H "Content-Type: application/json" \
  -d '{
    "preferredTripDuration": "DAY_TRIP",
    "regionIds": [1, 2],
    "themeIds": [10],
    "tagIds": [100, 101]
  }'
```

Get onboarding preference:

```bash
curl -i http://localhost:8080/api/users/me/preferences \
  -H "Authorization: Bearer {accessToken}"
```

Refresh token:

```bash
curl -i -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "{refreshToken}"
  }'
```

Logout:

```bash
curl -i -X POST http://localhost:8080/api/auth/logout \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "{refreshToken}"
  }'
```

Withdraw:

```bash
curl -i -X DELETE http://localhost:8080/api/users/me \
  -H "Authorization: Bearer {accessToken}"
```
