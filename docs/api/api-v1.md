# API v1 Draft

## Auth

| Method | Path | Auth | Description |
| --- | --- | --- | --- |
| POST | `/api/auth/signup` | Public | Create user account |
| POST | `/api/auth/login` | Public | Login and issue tokens |
| POST | `/api/auth/logout` | Required | Revoke refresh token |
| POST | `/api/auth/refresh` | Public | Reissue access token |

## Contents

| Method | Path | Auth | Description |
| --- | --- | --- | --- |
| GET | `/api/contents` | Public | List destination contents |
| GET | `/api/contents/{contentId}` | Public | Get destination detail with cards |
| GET | `/api/contents/hot` | Public | List HOT destination contents |

### Content Query Parameters

| Name | Description |
| --- | --- |
| `regionId` | Filter by region |
| `themeId` | Filter by theme |
| `tagIds` | Filter by tag list |
| `tripDurationType` | `DAY_TRIP` or `ONE_NIGHT_TWO_DAYS` |
| `page`, `size` | Pagination |
| `sort` | `popular`, `latest`, `bookmark`, `view` |

## Recommendations

| Method | Path | Auth | Description |
| --- | --- | --- | --- |
| GET | `/api/recommendations/feed` | Optional | Preference-based feed, fallback to popularity |
| GET | `/api/recommendations/related/{contentId}` | Public | Related places for a destination |
| GET | `/api/recommendations/roulette` | Public | Random candidates by condition |
| POST | `/api/preferences` | Required | Save onboarding preferences |

## Routes

| Method | Path | Auth | Description |
| --- | --- | --- | --- |
| GET | `/api/contents/{contentId}/routes` | Public | List saved routes for a destination |
| GET | `/api/routes/{routeId}` | Public | Get route detail and map points |

## Search

| Method | Path | Auth | Description |
| --- | --- | --- | --- |
| GET | `/api/search` | Public | Keyword search |
| GET | `/api/search/filter` | Public | Region, theme, tag, and duration filter search |

## Bookmarks

| Method | Path | Auth | Description |
| --- | --- | --- | --- |
| GET | `/api/bookmarks` | Required | List user's bookmarked contents |
| POST | `/api/bookmarks` | Required | Bookmark a content |
| DELETE | `/api/bookmarks/contents/{contentId}` | Required | Delete bookmark by content id |
| DELETE | `/api/bookmarks` | Required | Delete bookmarks in bulk |

## Categories

| Method | Path | Auth | Description |
| --- | --- | --- | --- |
| GET | `/api/themes` | Public | List themes with content count |
| GET | `/api/themes/{themeId}/sub-themes` | Public | List sub-themes |
| GET | `/api/tags` | Public | List tags |
| GET | `/api/tags/{tagId}/contents` | Public | List contents by tag |

## Internal Data APIs

These APIs are not part of the user-facing MVP app. They are for admin, parser, or batch jobs.

| Method | Path | Description |
| --- | --- | --- |
| POST | `/api/admin/contents/import` | Import public-data contents |
| POST | `/api/admin/routes/drafts` | Create route drafts from related-place candidates |
| PUT | `/api/admin/contents/{contentId}` | Update processed content |
| DELETE | `/api/admin/contents/{contentId}` | Hide or remove content |
