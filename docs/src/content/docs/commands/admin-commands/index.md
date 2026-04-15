---
title: "Admin Commands"
---

These commands are intended for staff and server owners. They cover creation, editing, debugging, migration, giveaways, and giving items directly to players.

## Command Groups

- [Create](./create.md): Create empty rarities, cards, series, types, packs, and upgrades.
- [Edit](./edit.md): Change stored values after creation.
- [Give](./give.md): Give packs and cards directly to players.
- [Giveaway](./giveaway.md): Broadcast rewards to all online players.
- [Resolve](./resolve.md): Resolve a player's UUID.
- [Debug](./debug.md): Inspect internal state and export debug bundles.
- [Migrate](./migrate.md): Move YAML-backed data into another storage backend.

## Permissions

Admin nodes are centered around `cards.admin`, with more specific nodes documented on [All Permissions](../all-permissions.md).

Examples:

- `cards.create.*`
- `cards.edit.*`
- `cards.give.*`
- `cards.giveaway.*`
- `cards.resolve`
- `cards.reload`
- `cards.admin.migrate`
