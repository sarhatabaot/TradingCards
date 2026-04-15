---
title: "List"
---

Use `/cards list` to view collection progress, available packs, and upgrade ids.

## Collection Views

### Default List

```text
/cards list
```

Shows all rarities for the executing player and highlights owned and shiny-owned cards.

### By Rarity

```text
/cards list rarity <rarityId>
/cards list player rarity <playerName> [rarityId]
```

Use these commands to focus on one rarity or to inspect another online player's collection progress.

### By Series

```text
/cards list series <seriesId>
/cards list player series <playerName> [seriesId]
```

This view is useful when you care about completion inside one series rather than one rarity.

## Packs and Upgrades

### Packs

```text
/cards list pack
```

Lists all known packs. If the economy is enabled and a pack has a price, the list includes the configured buy price.

### Upgrades

```text
/cards list upgrades
```

Shows the ids of all available upgrades.

## Permissions

- `cards.list`
- `cards.list.player`
- `cards.list.pack`
- `cards.list.upgrade`
