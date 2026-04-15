---
title: "Giveaway"
---

Use `/cards giveaway` to reward every online player at once.

## By Rarity

```text
/cards giveaway rarity <rarityId>
```

Each online player receives one random card from the selected rarity.

## By Series

```text
/cards giveaway series <seriesId> [rarityId]
```

If `rarityId` is omitted, each player receives a random card from the series. If both series and rarity are provided, the giveaway is restricted to cards matching both.

## By Entity

```text
/cards giveaway entity <entityType>
```

Uses the mob group associated with the supplied entity and performs the same style of giveaway used for natural mob drops.

## Permissions

- `cards.giveaway.rarity`
- `cards.giveaway.entity`
