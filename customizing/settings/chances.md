---
description: Chances are calculated when a mob or a player is killed.
---

# Chances

{% hint style="info" %}
You can find the default, commented file here: [https://github.com/sarhatabaot/TradingCards/blob/master/tradingcards-plugin/src/main/resources/settings/chances.yml](https://github.com/sarhatabaot/TradingCards/blob/master/tradingcards-plugin/src/main/resources/settings/chances.yml)
{% endhint %}

### How is the drop chance calculated?

First, we determine the mob type from the mob killed. This is done via a predetermined pool of mobs, you can find that pool in the following link: [MobGroupUtil](https://github.com/sarhatabaot/TradingCards/blob/master/tradingcards-plugin/src/main/java/net/tinetwork/tradingcards/tradingcardsplugin/utils/MobGroupUtil.java).

After the mob type is determined, we check the "general drop chance" in `chances.yml` :

```yaml
hostile-chance: 20000
neutral-chance: 5000
passive-chance: 1000
boss-chance: 100000
all-chance: 5000
```

We then generate a random number between 0-100,000 and check if that number is below the chance. If it is, a card is dropped.

#### For example:

**Cards Drops:** \
Player kills a zombie. Type determined is "hostile". Hostile chance is 20,000. Generated number is 12,456. \
12456 < 20000 = drop\
\
**No Drop:**\
****Player kills a zombie. Type determined is "hostile". Hostile chance is 20,000. Generated number is 40,000. \
40000 > 20000 = no drop

### How is the card chosen?

We choose the rarity to drop a card from through a "weight" system. The chances you define in chances.yml define the weight. The higher the weight is, the higher the chance that rarity will drop.

```yaml
common:
  hostile: 100000
  neutral: 100000
  passive: 100000
uncommon:
  hostile: 20000
  neutral: 10000
  passive: 5000
rare:
  hostile: 1000
  neutral: 500
very-rare:
  hostile: 10
  boss: 100000
legendary:
  hostile: 1
  boss: 50000
```

We use a CollectionSampler using the weights of all rarities as a sampler. For example, taking the "hostile" drop, we will pass the values to our sampler and then randomly return the rarity id.

If we ommit a type from the section above, it will not get dropped from that type.
