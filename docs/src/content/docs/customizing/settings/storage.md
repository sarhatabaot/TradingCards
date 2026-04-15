---
title: "Storage"
---

:::note
The default commented file is in the plugin jar at `settings/storage.yml`. The old GitBook link for this page pointed to `messages.yml`; that was incorrect.
:::

`storage.yml` controls whether TradingCards stores data in flat files or MySQL.

## Storage Modes

- `YAML`: Stores data in plugin files. Simple to manage and good for smaller servers.
- `MYSQL`: Stores data in a database. Better for larger installs and environments that already use SQL-backed plugins.

## Key Options

- `storage-type`: `YAML` or `MYSQL`
- `yaml.default-file`: Default file used for card definitions in YAML mode
- `sql.first-time-values`: Generates initial SQL-side values on first setup
- `database.address`, `port`, `database`, `username`, `password`: Connection settings
- `database.table-prefix`: Prefix for created tables
- `database-migration.default-series-id`: Fallback series id for migration flows

## Migration Notes

If you are moving existing YAML data into SQL, review the [YAML to SQL migration guide](../../migration/5.6.x-yaml-greater-than-sql.md) before switching the storage type on a live server.
