# Drop Pools

## Per-Mob Drop Pools

This page explains how to configure per-mob weighted card drops using `settings/drop-pools.yml`.

### What it does

* Lets you define custom drop pools per entity type (for example `zombie`, `skeleton`, `ender_dragon`).
* Supports optional custom-name matching (`namecheck`) for custom mobs.
* Supports weighted outcomes:
* `card_<cardId>` to drop a specific card id (random active version across rarity/series).
* `rarity_<rarityId>` to drop a random active card from that rarity.
* Supports `dropmin` and `dropmax` to drop multiple cards per kill.
* Supports optional per-pool `dropchance` (out of 100,000).

If no pool matches, the plugin uses the existing default drop logic from `chances.yml`.

### File location

* `plugins/TradingCards/settings/drop-pools.yml`

Default template is shipped at:

* `tradingcards-plugin/src/main/resources/settings/drop-pools.yml`

### Basic format

```yaml
config-version: 1

zombie:
  namecheck: "Jeffrey" # optional
  drops:
    enabled: true
    dropmin: 1
    dropmax: 1
    dropchance: 100000 # optional, 1..100000. If omitted, default mob-group chance is used.
    card_zombie:
      dropchance: 9900
    rarity_common:
      dropchance: 100
```

### Option reference

* Entity key:
* Any Bukkit `EntityType` id, case-insensitive (`zombie`, `skeleton`, `creeper`).
* `namecheck`:
* Optional exact name match for the mob custom name.
* Color codes are supported in matching.
* `drops.enabled`:
* Enables/disables the pool.
* `drops.dropmin`:
* Minimum number of card rolls to drop when this pool triggers.
* `drops.dropmax`:
* Maximum number of card rolls to drop when this pool triggers.
* `drops.dropchance`:
* Optional pool trigger chance out of `100000`.
* If omitted, plugin uses normal hostile/neutral/passive/boss chance from `chances.yml`.
* Entry keys under `drops`:
* `card_<cardId>` with `dropchance` weight.
* `rarity_<rarityId>` with `dropchance` weight.
* Weights are relative, not percentages.

### Weight behavior

* Entries are selected by weight.
* Example:
* `card_zombie: 9900`
* `rarity_common: 100`
* Total is `10000`, so `card_zombie` has 99% weight and `rarity_common` has 1% weight.

### Notes and validation

* Invalid entity keys are ignored with a warning.
* Invalid entry keys are ignored with a warning.
* Non-positive entry weights are ignored.
* If a pool is enabled but has no valid entries, it logs a warning and drops nothing from that pool.
* Rarity ids in `rarity_<id>` are matched case-insensitively.

### Reloading

* After changes, run your plugin reload command so configs are reloaded.
* TradingCards internal reload path reloads `drop-pools.yml`.
