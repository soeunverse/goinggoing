# TDD Case Plan

## Development Flow

1. Define success, failure, and edge cases.
2. RED: write tests first and confirm failure.
3. GREEN: implement the smallest production code needed to pass.
4. Document: add Swagger annotations and comments where useful.

## Phase 1. Common API Response and Exception

### Success Cases

| Case | Given | When | Then |
| --- | --- | --- | --- |
| Success with data | Data exists | `ApiResponse.success(data)` | `success=true`, `data=data`, `message=null` |
| Success with message | Data and message exist | `ApiResponse.success(data, message)` | `success=true`, `data=data`, `message=message` |
| Success without data | No response body is needed | `ApiResponse.successWithoutData()` | `success=true`, `data=null`, `message=null` |

### Failure Cases

| Case | Given | When | Then |
| --- | --- | --- | --- |
| Failure from error code | Error code exists | `ApiResponse.failure(errorCode)` | `success=false`, `data=null`, expected `errorCode/message` |
| Business exception | Domain rule fails | `BusinessException(errorCode)` | exception keeps the same error code |

### Edge Cases

| Case | Given | When | Then |
| --- | --- | --- | --- |
| Custom failure message | Client-facing message needs override | `ApiResponse.failure(errorCode, message)` | default message is overridden |
| Unexpected exception | Unknown server error occurs | global handler receives exception | response uses `INTERNAL_SERVER_ERROR` |

## Phase 2. User Signup

### Target API

- `POST /api/auth/signup`

### Success Cases

| Case | Given | When | Then |
| --- | --- | --- | --- |
| Signup success | New email, valid password, nickname | User signs up | User is saved as `ACTIVE` and response contains user id/email/nickname |
| Password encoding | Valid signup request | User is saved | Saved password is not raw text and password encoder can match it |

### Failure Cases

| Case | Given | When | Then |
| --- | --- | --- | --- |
| Duplicate email | Email already exists | User signs up | `EMAIL_ALREADY_EXISTS` business exception |
| Short password | Password length is less than 8 | User signs up | `INVALID_PASSWORD` business exception |
| Password without alphabet | Password has numbers only | User signs up | `INVALID_PASSWORD` business exception |
| Password without number | Password has alphabets only | User signs up | `INVALID_PASSWORD` business exception |
| Blank nickname | Nickname is blank | User signs up | `INVALID_NICKNAME` business exception |

### Edge Cases

| Case | Given | When | Then |
| --- | --- | --- | --- |
| Nickname duplicate | Same nickname exists | User signs up | Signup succeeds because nickname duplication is allowed |
| Password length max | Password is longer than 100 characters | User signs up | `INVALID_PASSWORD` business exception |

## Phase 3. Category Lookup

### Target APIs

- `GET /api/themes`
- `GET /api/themes/{themeId}/sub-themes`
- `GET /api/tags`

### Success Cases

- Published theme list is returned in display order.
- Sub-themes are returned only for the requested theme.
- Tags are returned in display order.

### Failure Cases

- Theme does not exist when requesting sub-themes.

### Edge Cases

- Empty result returns an empty list, not `null`.
- Invalid path variable returns `400 Bad Request`.
