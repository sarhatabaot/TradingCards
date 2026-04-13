package net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mythicmobs;


import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mythicmobs.chances.LevelChances;
import net.tinetwork.tradingcards.tradingcardsplugin.listeners.DropListener;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.cards.CompositeCardKey;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import org.apache.commons.rng.sampling.CollectionSampler;
import org.apache.commons.rng.sampling.DiscreteProbabilityCollectionSampler;
import org.apache.commons.rng.simple.RandomSource;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MythicMobsListener implements Listener {
    private final TradingCards plugin;
    private final MythicMobsConfig mythicMobsConfig;

    public MythicMobsListener(TradingCards plugin, MythicMobsConfig mythicMobsConfig) {
        this.plugin = plugin;
        this.mythicMobsConfig = mythicMobsConfig;
    }

    @EventHandler
    public void onMythicMobDeath(@NotNull MythicMobDeathEvent e) {
        final Player killer = (Player) e.getKiller();
        final WeakReference<World> world = new WeakReference<>(e.getEntity().getWorld());

        //Do Validations
        if (killer == null) return;
        if (!plugin.getPlayerDenylist().isAllowed(killer)) return;
        if (!plugin.getWorldDenylist().isAllowed(world.get())) return;

        double mobLevel = e.getMobLevel();
        LevelChances level = getQualifiedLevel(mobLevel);

        final String rarityName = getRandomRarityId(level);
        //Get the card
        TradingCard randomCard = plugin.getCardManager().getRandomActiveCardByRarity(rarityName);
        if (randomCard instanceof EmptyCard) {
            plugin.debug(DropListener.class, "EmptyCard for some reason, rarity=%s".formatted(rarityName));
            return;
        }

        boolean isShiny = randomCard.hasShiny() && CardUtil.calculateIfShiny(false);
        plugin.debug(MythicMobsListener.class, InternalDebug.DropListener.ADDED_CARD.formatted(CompositeCardKey.fromCard(randomCard)));
        if (plugin.getGeneralConfig().collectorBookEnabled()) {
            CardUtil.dropItem(killer, randomCard.build(isShiny));
            return;
        }
        e.getDrops().add(randomCard.build(isShiny));
    }

    public String getRandomRarityId(LevelChances levelChances) {
        Map<String, Double> rarityWeights = getMythicRarityWeightMap(levelChances);
        if (new HashSet<>(rarityWeights.values()).size() == 1) {
            //when everything is equal chance...
            CollectionSampler<String> sampler = new CollectionSampler<>(RandomSource.MWC_256.create(), rarityWeights.keySet());
            return sampler.sample();
        }

        DiscreteProbabilityCollectionSampler<String> sampler = new DiscreteProbabilityCollectionSampler<>(RandomSource.MWC_256.create(), rarityWeights);
        return sampler.sample();
    }

    private Map<String, Double> getMythicRarityWeightMap(LevelChances levelChances) {
        Map<String, Double> rarityWeight = new HashMap<>(); //id, weight
        for (String rarity : plugin.getRarityManager().getRarityIds()) {
            rarityWeight.put(rarity, (double) levelChances.getChance(rarity));
        }
        return rarityWeight;
    }

    private LevelChances getQualifiedLevel(double mobLevel) {
        return mythicMobsConfig.levelChances().keySet().stream().filter(level -> level <= mobLevel).max(Double::compare).map(mythicMobsConfig::getChances).orElse(null); //needs testing
    }
}
