---
title: "Edit"
---

Use `/cards edit` to change stored values after an object exists. Most edit targets also support a GUI editor when run by a player without extra arguments.

## Major Edit Targets

### Card

```text
/cards edit card <rarityId> <seriesId> <cardId> <field> <value>
```

Supported fields:

- `DISPLAY_NAME`
- `CUSTOM_MODEL_DATA`
- `BUY_PRICE`
- `SELL_PRICE`
- `INFO`
- `SERIES`
- `HAS_SHINY`
- `TYPE`
- `CURRENCY_ID`

### Rarity

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

### Series

```text
/cards edit series <seriesId> <field> <value>
```

Supported fields:

- `DISPLAY_NAME`
- `MODE`
- `COLORS`

The `COLORS` field accepts space-separated assignments such as `info=&7 about=&f rarity=&6`.

### Pack

```text
/cards edit pack <packId> <field> <value>
```

Supported fields:

- `PRICE`
- `PERMISSION`
- `DISPLAY_NAME`
- `CONTENTS`
- `TRADE`
- `CURRENCY_ID`

For `CONTENTS` and `TRADE`, use `line=value`, for example:

```text
/cards edit pack starter CONTENTS 1=common:3:default
/cards edit pack starter TRADE 2=delete
```

### Custom Type

```text
/cards edit type <typeId> <field> <value>
```

Supported fields:

- `TYPE`
- `DISPLAY_NAME`

### Upgrade

```text
/cards edit upgrade <upgradeId> <REQUIRED|RESULT> <rarityId> <amount> <seriesId>
```

## Tip

The edit command is the safest way to mutate data because it also refreshes the plugin caches after the update.
