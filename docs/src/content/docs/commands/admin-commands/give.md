---
title: "Give"
---

Use `/cards give` to deliver cards or packs directly to players.

## Card Giving

### Give Yourself a Card

```text
/cards give card self <rarityId> <seriesId> <cardId> <true|false>
```

### Give a Player a Card

```text
/cards give card player <playerName> <rarityId> <seriesId> <cardId> <true|false>
```

Notes:

- The final boolean controls whether the given card should be shiny.
- Shiny cards also require `cards.give.card.shiny`.
- The target player must be online.

## Pack Giving

```text
/cards give pack <playerName> <packId>
```

Gives one pack item to the target player.

## Random Giving

```text
/cards give random entity <playerName> <entityType>
/cards give random rarity <playerName> <rarityId>
/cards give random series <playerName> <seriesId>
```

These subcommands choose a random matching card and give it to the target player.

## Permissions

- `cards.give`
- `cards.give.card`
- `cards.give.card.player`
- `cards.give.card.shiny`
- `cards.give.pack`
- `cards.give.random.entity`
- `cards.give.random.rarity`
