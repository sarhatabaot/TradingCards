---
title: "Series"
---

Series group cards into themed collections. Players can browse progress by series, and drops or upgrades can target a specific series.

## Creating a Series

```text
/cards create series <seriesId>
```

This also creates the associated color-series entry used for lore formatting.

## Editing a Series

```text
/cards edit series <seriesId> <field> <value>
```

Supported fields:

- `DISPLAY_NAME`
- `MODE`
- `COLORS`

## Modes

The series mode controls whether the series is active. The available values come from the plugin's `Mode` enum, such as `ACTIVE`, `DISABLED`, and `SCHEDULED`.

## Colors

The `COLORS` editor accepts space-separated assignments:

```text
/cards edit series default COLORS info=&7 about=&f type=&e series=&b rarity=&6
```

These values control how card lore is colored for cards in the series.
