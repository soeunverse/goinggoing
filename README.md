# goinggoing

GoingGoing is a travel curation service for spontaneous users who want to choose one destination and receive card-style content plus a saved recommended route.

## Project Structure

```text
goinggoing/
  src/main/java/com/goinggoing/goinggoing/
    domain/         feature domains
      content/
        controller/
        dto/
        entity/
        repository/
        service/
      route/
      user/
      auth/
      bookmark/
      recommendation/
      category/
      search/
    infra/          external API and technical integration code
      kto/
    global/         shared config, exception, response, security, and utility code
  frontend/         reserved for the web client
  parser/           reserved for public-data ingestion and preprocessing
  docs/             planning, API, and database design documents
```

See `docs/architecture/package-structure.md` for package ownership rules.

## MVP Scope

- Destination content list/detail
- Card-news style content cards
- Preference-based feed and roulette recommendation
- Saved day-trip or one-night route lookup
- Login-required bookmarks
- Theme, tag, search, and filter support
- Korea Tourism Organization public-data sync into the service database
