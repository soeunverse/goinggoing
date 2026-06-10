# Database Notes

## Content

`contents` is the main destination table. A content row represents one specific destination that the user can choose, such as Sungsimdang, Gwangalli Beach, or Jeonju Hanok Village.

Public API data is stored with source fields so that processed service content can be traced back to the original data.

## Cards

`content_cards` stores the card-news data shown in the carousel. The frontend can show three cards at once, but the database supports a variable number of ordered cards per content.

## Routes

Routes are saved recommendations, not real-time user-facing AI generations in the MVP. Route drafts can be generated or prepared using related-place public data, then published after processing.

## Public Data

- `related_places` stores candidates from Korea Tourism Organization related-tourist-attraction data.
- `content_demand_metrics` stores regional demand signals that can support HOT and recommendation ranking.
- `external_sync_logs` stores parser or batch import results.
