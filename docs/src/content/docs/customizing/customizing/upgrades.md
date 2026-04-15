---
title: "Upgrades"
---

Upgrades define card exchanges, such as turning several low-rarity cards from one series into a higher-rarity result.

## Creating an Upgrade

```text
/cards create upgrade <upgradeId> <requiredPackEntry> <resultPackEntry>
```

Pack entry format:

```text
<rarityId>:<amount>:<seriesId>
```

Example:

```text
/cards create upgrade uncommon_to_rare common:5:default rare:1:default
```

## Editing an Upgrade

```text
/cards edit upgrade <upgradeId> <REQUIRED|RESULT> <rarityId> <amount> <seriesId>
```

## Player Usage

Once configured, players can use `/cards upgrade` or `/cards upgrade max` to consume matching cards and receive the configured result rarity.
