---
title: "Sell"
---

Use `/cards sell` to sell the card stack in your main hand.

## Syntax

```text
/cards sell
```

## Behavior

- The item in your main hand must be a TradingCards card.
- The command uses the card's configured `sell-price`.
- The full stack in hand is sold at once.
- Shiny cards cannot be sold.
- If the card has a sell price of `0`, the command does nothing except report that it cannot be sold.

When the sale succeeds, the player receives money through the configured economy provider and the item stack is removed from the held slot.

## Permission

- `cards.sell`

## Related

- [Worth](./worth.md)
- [Buy](./buy.md)
