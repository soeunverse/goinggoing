# GoingGoing MVP Scope

## Service Definition

GoingGoing recommends one destination for users who want to travel spontaneously, then provides card-news content and saved routes centered on that destination.

## Target User

- Users who do not want to plan every detail
- Users who can spend money and time on a spontaneous day trip or one-night trip
- Users who prefer choosing one strong destination first, then checking nearby plans

## Core Flow

1. User selects onboarding preferences.
2. User browses destination cards in a feed or roulette.
3. User opens one destination detail page.
4. User reads card-news content.
5. User checks saved recommended routes for day trip or one-night trip.
6. User logs in when using bookmarks or other personal features.

## Public Data Strategy

- Store public API data in the service database before serving it to the app.
- Use place-level Korea Tourism Organization data as the source for main contents.
- Use related-tourist-attraction data as route and related-place candidates.
- Use regional tourism demand data as a helper signal for HOT and recommendation ranking.
- Store processed card content separately from raw public data.

## MVP In

- Signup, login, logout, token refresh
- Content list, detail, HOT list
- Content cards for carousel UI
- Feed recommendation
- Roulette recommendation
- Related content recommendation
- Saved route list and route detail
- Keyword search and filter search
- Bookmark list/add/delete
- Theme, sub-theme, and tag lookup

## MVP Out

- User-facing real-time AI route generation
- Guest bookmarks
- User-generated content creation
- Full admin UI
- Real-time popular search ranking
- Recent search history as a primary feature

## Login Policy

- Content, recommendation, route, search, theme, and tag APIs are public.
- Bookmark and user preference APIs require login.
- Non-login users can browse first and are prompted to log in when they try to bookmark or save personal data.
