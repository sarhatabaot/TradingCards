package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import net.tinetwork.tradingcards.api.manager.RarityManager;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TradingRarityManager implements RarityManager {
    private final TradingCards plugin;

    public TradingRarityManager(final TradingCards plugin) {
        this.plugin = plugin;
    }

    @Nullable
    public Rarity getRarity(final String id) {
        return plugin.getStorage().getRarityById(id);
    }

    @Override
    public List<Rarity> getRarities() {
        return plugin.getStorage().getRarities();
    }

    @Override
    public boolean containsRarity(final String rarityId) {
        //todo although this can be done, we could check if it exists first
        return getRarity(rarityId) == null;
    }
}