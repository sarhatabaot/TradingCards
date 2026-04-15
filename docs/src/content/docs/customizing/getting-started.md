---
title: "Getting Started"
---

You can use TradingCards out of the box with the default configuration, but most servers will want to adjust the base item materials, economy behavior, and card data before going live.

## Recommended Setup Order

1. Review the files in [`settings/`](./settings/index.md), especially `general.yml`, `chances.yml`, and `storage.yml`.
2. Decide whether you are staying on YAML storage or moving to MySQL.
3. Set your default materials for cards, decks, and packs.
4. Create or import your rarities, series, cards, and packs.
5. Test the main player flows: drops, `/cards list`, buying, selling, and deck usage.

## First Files To Check

- [`Settings`](./settings/index.md) controls plugin-wide behavior.
- [`Cards`](./customizing/cards.md), [`Rarities`](./customizing/rarities.md), and [`Series`](./customizing/series.md) define your collectible content.
- [`Migration`](../migration/5.6.x-yaml-greater-than-sql.md) helps if you are moving older data formats forward.

:::note
If you edit YAML files directly, save the file and run `/cards reload` before testing. Command-based edits are safer for day-to-day changes on live servers.
:::
