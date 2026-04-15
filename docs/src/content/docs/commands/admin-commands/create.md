---
title: "Create"
---

Use `/cards create` to create new records quickly, then fill in the details later with `/cards edit`.

## Available Targets

### Rarity

```text
/cards create rarity <rarityId>
```

Creates a new rarity entry with default values.

### Card

```text
/cards create card <cardId> <rarityId> <seriesId>
```

Creates a card in an existing rarity and series.

### Pack

```text
/cards create pack <packId>
```

Creates an empty booster pack entry.

### Series

```text
/cards create series <seriesId>
```

Creates the series and its color-series entry.

### Custom Type

```text
/cards create type <typeId> <defaultType>
```

`defaultType` must be one of the built-in mob groups such as `boss`, `hostile`, `neutral`, `passive`, or `all`.

### Upgrade

```text
/cards create upgrade <upgradeId> <requiredPackEntry> <resultPackEntry>
```

Pack entry syntax follows the same `rarityId:amount:seriesId` format used elsewhere.

## Recommended Flow

1. Create the object.
2. Use `/cards edit ...` to set names, prices, colors, contents, and other fields.
3. Run `/cards reload` if you also changed files manually.
