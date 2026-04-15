---
title: "Cards"
---

Cards are the core collectible objects in TradingCards. Each card belongs to exactly one rarity and one series.

The physical item used for cards is controlled by `card-material` in [`general.yml`](../settings/general.md). That value can be either:

- A vanilla Bukkit material such as `PAPER`
- A custom plugin item using the `plugin:id` format

For ItemsAdder specifically, use:

```yaml
card-material: itemsadder:namespace:item_id
```

In that case, the plugin id is `itemsadder` and the item id portion is `namespace:item_id`.

:::note
TradingCards currently ships native support for ItemsAdder custom items. Future plugin integrations follow the same `plugin:id` pattern, so configs do not need a different material format per plugin.
:::

## Creating Cards

Use the command flow:

```text
/cards create card <cardId> <rarityId> <seriesId>
```

Then edit the card:

```text
/cards edit card <rarityId> <seriesId> <cardId> <field> <value>
```

## Common Card Fields

- `DISPLAY_NAME`: Player-facing card name.
- `BUY_PRICE`: Price used by `/cards buy card`.
- `SELL_PRICE`: Price used by `/cards sell`.
- `INFO`: Extra descriptive text shown on the card.
- `SERIES`: Move the card to another series.
- `TYPE`: Assign the card to a drop type.
- `HAS_SHINY`: Allow shiny generation for the card.
- `CUSTOM_MODEL_DATA`: Assign a resource-pack model id.
- `CURRENCY_ID`: Economy currency used for buy and sell operations.

## Tips

- Keep `cardId` stable after release if players already own the card.
- Use [Series](./series.md) to organize drops and collection views.
- If you use resource-pack items, make sure your card base item and custom model data are consistent.
- If you want custom plugin-backed card items, configure the shared card base item in [`general.yml`](../settings/general.md) before creating large batches of cards.
