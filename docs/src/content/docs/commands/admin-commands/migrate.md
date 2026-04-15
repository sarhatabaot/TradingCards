---
title: "Migrate"
---

Use `/cards migrate` when moving data from YAML storage into another configured storage backend, such as SQL.

## Important Notes

- Back up your plugin folder before migrating.
- Change the storage type first so TradingCards knows the target backend.
- Data and deck migration are separate commands.
- Restart the server after migrating data.

## Commands

### Show Migration Help

```text
/cards migrate
/cards migrate all
```

Shows warnings and the follow-up commands needed for the current target storage type.

### Migrate Data

```text
/cards migrate data confirm
```

Migrates rarities, series, upgrades, packs, custom types, and cards.

### Migrate Decks

```text
/cards migrate deck confirm
```

Migrates stored player deck data.

## Permission

- `cards.admin.migrate`
