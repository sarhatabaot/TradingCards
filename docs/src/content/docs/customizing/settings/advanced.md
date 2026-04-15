---
title: "Advanced"
---

:::note
The default commented file is in the plugin jar at `settings/advanced.yml`. Most servers do not need to change it.
:::

`advanced.yml` is primarily for cache tuning.

## Cache Settings

Each section controls one cache used by the plugin:

- `rarity`
- `series`
- `cards`
- `types`
- `packs`
- `upgrades`

Each cache supports:

- `max-cache-entries`: Maximum number of cached records before eviction
- `refresh-after-write`: How many minutes an entry stays fresh after being updated

Unless you are diagnosing memory pressure or stale data behavior, keep the defaults.
