package net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mythicmobs;


import java.util.Map;

public record MythicMobsLevelChances(int shinyVersionChance, int dropChance, Map<String, Integer> rarityChances) {

    public Integer getChance(final String rarityId) {
        return rarityChances.get(rarityId);
    }
}
