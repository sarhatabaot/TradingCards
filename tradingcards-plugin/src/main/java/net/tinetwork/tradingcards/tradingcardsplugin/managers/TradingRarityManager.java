package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import net.tinetwork.tradingcards.api.manager.RarityManager;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.jetbrains.annotations.Nullable;

public class TradingRarityManager implements RarityManager {
    private final TradingCards plugin;

    public TradingRarityManager(final TradingCards plugin) {
        this.plugin = plugin;
    }

    @Nullable
    public Rarity getRarity(final String id) {
        return plugin.getStorage().getRarityById(id);
    }
}
