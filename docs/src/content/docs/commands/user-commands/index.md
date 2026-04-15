---
title: "User Commands"
---

These are the day-to-day commands players use to inspect cards, buy or sell them, browse collections, and perform upgrades.

Most user commands are available through `/cards ...`. The separate `/deck <number>` command is documented on the [Deck](./deck.md) page.

## Command Groups

- [Worth](./worth.md): Show the buy and sell value of the card in your hand.
- [Buy](./buy.md): Buy booster packs or specific cards.
- [Sell](./sell.md): Sell the card stack in your main hand.
- [List](./list.md): Browse owned cards, series progress, packs, and upgrades.
- [Info](./info.md): Inspect detailed information about cards, packs, rarities, series, types, mobs, and upgrades.
- [Upgrade](./upgrade.md): Trade lower-rarity cards into higher-rarity results based on configured upgrades.

## Permissions

Most user-facing permissions are listed on the [All Permissions](../all-permissions.md) page. The most common ones are:

- `cards.worth`
- `cards.buy`, `cards.buy.pack`, `cards.buy.card`
- `cards.sell`
- `cards.list`, `cards.list.player`, `cards.list.pack`, `cards.list.upgrade`
- `cards.info`, plus the more specific `cards.info.*` nodes
