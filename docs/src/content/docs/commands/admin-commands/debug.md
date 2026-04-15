---
title: "Debug"
---

Use `/cards debug` when you need to inspect plugin state or collect files for support.

## Available Subcommands

### Zip

```text
/cards debug zip
```

Creates `debug.zip` in the plugin data folder. The zip includes cards, data, lists, and settings while excluding `storage.yml`.

### Show Cache

```text
/cards debug showcache all
/cards debug showcache active
```

Shows cached card keys from storage. This is useful when tracking down missing cards or stale cache data.

### Modules

```text
/cards debug modules
```

Lists configured soft dependencies and whether Paper currently sees them as loaded.

### Packs and Rarities

```text
/cards debug packs
/cards debug rarities
/cards debug rarities-series <rarityId> <seriesId>
```

Use these to verify pack ids, rarity ids, and cards inside a rarity/series pair.

### Exists

```text
/cards debug exists <cardId> <rarityId> <seriesId>
```

Checks whether a specific card exists in the loaded card manager.
