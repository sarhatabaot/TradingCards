---
title: "Info"
---

Use `/cards info` to inspect stored data without opening the YAML or database directly.

## Available Subcommands

### Card

```text
/cards info card <rarityId> <seriesId> <cardId>
```

Shows the card id, series, rarity, display name, buy price, sell price, currency, about text, and info text.

### Pack

```text
/cards info pack <packId>
```

Shows the pack id, display name, contents, trade requirements, currency id, and buy price.

### Type

```text
/cards info type <typeId>
```

Shows the custom drop type id, display name, and backing mob group.

### Series

```text
/cards info series <seriesId>
```

Shows the series id, display name, mode, and configured color settings.

### Rarity

```text
/cards info rarity <rarityId>
```

Shows rarity configuration such as color, prices, and rewards.

### Mob

```text
/cards info mob <entityType>
```

Shows which TradingCards mob group a Bukkit entity belongs to.

### Upgrade

```text
/cards info upgrade <upgradeId>
```

Shows the required input cards and result cards for an upgrade rule.

## Permissions

- `cards.info`
- `cards.info.card`
- `cards.info.pack`
- `cards.info.type`
- `cards.info.series`
- `cards.info.rarity`
- `cards.info.mob`
- `cards.info.upgrade`
