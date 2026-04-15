---
title: "Upgrade"
---

Use `/cards upgrade` to convert one configured set of cards into another.

Upgrades are defined in storage and match against the card held in the player's main hand. The held card helps the plugin determine which upgrade rule to use when no explicit upgrade id is provided.

## Syntax

### Default

```text
/cards upgrade [amount] [upgradeId]
```

Behavior:

- If `upgradeId` is omitted, TradingCards tries to match an upgrade from the held card's rarity and series.
- If `amount` is omitted, it defaults to `1`.
- The player must have enough matching input cards in inventory.

### Maximum Possible

```text
/cards upgrade max
```

This calculates the highest number of upgrades the player can perform from their current inventory and then runs the upgrade automatically.

## Notes

- Upgrades only consume non-shiny matching cards.
- Results are dropped to the player as generated card items.
- The result card is random within the configured output rarity.

## Related

- [List](./list.md)
- [All Commands](../all-commands.md)
