package net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mythicmobs.chances;


import java.util.Map;

public record LevelChances(double level, int shinyVersionChance, int dropChance, Map<String, Integer> rarityChances) {

    public Integer getChance(final String rarityId) {
        return rarityChances.get(rarityId);
    }
}
