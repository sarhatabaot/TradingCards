---
title: "Buy"
---

Use `/cards buy` to purchase either booster packs or individual cards.

## Buying Packs

Syntax:

```text
/cards buy pack <packId> [amount]
```

Notes:

- `amount` is optional and defaults to `1`.
- Packs must exist and must either have a positive buy price or a configured trade-in requirement.
- If the pack defines trade cards, the player must have the required cards in inventory before the trade succeeds.
- If `closed-economy` is enabled, the withdrawn money is deposited into the configured server account.

Example:

```text
/cards buy pack starter 3
```

## Buying Cards

Syntax:

```text
/cards buy card <rarityId> <seriesId> <cardId>
```

This buys a single non-shiny card directly from storage using the card's configured buy price and currency.

Example:

```text
/cards buy card common default zombie
```

## Permissions

- `cards.buy`
- `cards.buy.pack`
- `cards.buy.card`

## Related

- [Sell](./sell.md)
- [List](./list.md)
