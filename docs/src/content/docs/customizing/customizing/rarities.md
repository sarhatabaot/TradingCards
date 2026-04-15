---
title: "Rarities"
---

Rarities define pricing, sort order, display styling, and optional rewards.

## Creating a Rarity

```text
/cards create rarity <rarityId>
```

## Editing a Rarity

```text
/cards edit rarity <rarityId> <field> <value>
```

Supported fields:

- `DISPLAY_NAME`
- `DEFAULT_COLOR`
- `BUY_PRICE`
- `SELL_PRICE`
- `ADD_REWARD`
- `REMOVE_REWARD`
- `REMOVE_ALL_REWARDS`
- `CUSTOM_ORDER`

## Practical Use

- Use `CUSTOM_ORDER` to control how rarities are shown in lists.
- Use `DEFAULT_COLOR` to keep titles and rarity displays readable.
- Configure `BUY_PRICE` and `SELL_PRICE` if you want player trading with the server.
