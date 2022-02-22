package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import net.tinetwork.tradingcards.api.manager.RarityManager;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class TradingRarityManager implements RarityManager {
    private final TradingCards plugin;
    public static final Rarity EMPTY_RARITY = new Rarity("empty","empty","",0.00,0.00, Collections.singletonList(""));
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
        for(Rarity rarity: getRarities()) {
            if(rarity.getName().equals(rarityId))
                return true;
        }
        return false;
    }
}
