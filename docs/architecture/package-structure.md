# Package Structure

The backend uses a practical DDD package structure for a small team project. Each business feature appears once under `domain`, then contains its own controller, DTO, entity, repository, and service packages.

This avoids duplicated feature names like `bookmark` appearing under every technical layer.

## Root

```text
com.goinggoing.goinggoing
  domain
  infra
  global
```

## Domain Packages

```text
domain
  auth
  user
  content
  route
  bookmark
  recommendation
  category
  search
```

Each domain package may contain:

```text
controller
dto
entity
repository
service
```

Example:

```text
domain/bookmark
  controller
  dto
  entity
  repository
  service
```

## Package Responsibilities

### controller

REST API entry points.

Examples:

- `ContentController`
- `BookmarkController`
- `RouteController`

### dto

Request and response objects used by controllers and services.

Examples:

- `ContentResponse`
- `BookmarkCreateRequest`
- `RouteDetailResponse`

### entity

JPA entities and enums owned by the domain.

Examples:

- `Content`
- `ContentCard`
- `Bookmark`
- `Route`

### repository

Spring Data repositories and repository query contracts.

Examples:

- `ContentRepository`
- `BookmarkRepository`
- `RouteRepository`

### service

Business use cases and domain logic orchestration.

Examples:

- `ContentService`
- `BookmarkService`
- `RecommendationService`

## Infra

`infra` contains integrations that are not owned by one domain.

```text
infra/kto
  client
  dto
  service
```

Use this package for Korea Tourism Organization API clients, import jobs, and preprocessing services.

## Global

`global` contains shared code that should not belong to a specific domain.

```text
global
  config
  exception
  response
  security
  util
```

Do not place feature-specific business logic in `global`.

## Current Recommended Tree

```text
src/main/java/com/goinggoing/goinggoing
  GoinggoingApplication.java
  domain
    auth
      controller
      dto
      service
    user
      controller
      dto
      entity
      repository
      service
    content
      controller
      dto
      entity
      repository
      service
    route
      controller
      dto
      entity
      repository
      service
    bookmark
      controller
      dto
      entity
      repository
      service
    recommendation
      controller
      dto
      service
    category
      controller
      dto
      entity
      repository
      service
    search
      controller
      dto
      entity
      repository
      service
  infra
    kto
      client
      dto
      service
  global
    config
    exception
    response
    security
    util
```
