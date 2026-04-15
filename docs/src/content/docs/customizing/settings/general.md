---
title: "General"
---

:::note
The default commented file is in the plugin jar at `settings/general.yml`. The source version is also available on GitHub in `tradingcards-plugin/src/main/resources/settings/general.yml`.
:::

`general.yml` controls the default materials, display formats, deck behavior, rewards, and plugin support features used across the plugin.

## Common Options

- `debug-mode`: Enables verbose debug logging. Default: `false`
- `card-material`: Base item used for cards
- `booster-pack-material`: Base item used for packs
- `deck-material`: Base item used for decks
- `use-deck-item`: If `true`, `/deck` gives a deck item. If `false`, it opens the deck directly.
- `deck-rows`: Controls deck size
- `allow-rewards`: Enables rarity reward collection
- `collector-book-enabled`: Auto-collects cards instead of dropping them physically
- `spawner-block`: Prevents card drops from spawned mobs

## Material Formats

The material fields accept either vanilla materials or custom plugin item ids.

```yaml
card-material: PAPER
deck-material: BOOK
booster-pack-material: BOOK
```

Custom plugin items use the `plugin:id` format. For ItemsAdder, the syntax is:

```yaml
card-material: itemsadder:namespace:item_id
deck-material: itemsadder:namespace:deck_item
booster-pack-material: itemsadder:namespace:pack_item
```

:::note
ItemsAdder support is currently built in. Additional custom item plugins are expected to follow the same `plugin:id` pattern as support is added.
:::
