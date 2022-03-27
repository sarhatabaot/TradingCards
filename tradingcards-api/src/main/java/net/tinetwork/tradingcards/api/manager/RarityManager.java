package net.tinetwork.tradingcards.api.manager;

import net.tinetwork.tradingcards.api.model.Rarity;

import java.util.List;

/**
 * @author sarhatabaot
 */
public interface RarityManager {
    Rarity getRarity(final String rarityId);
    List<Rarity> getRarities();
    boolean containsRarity(final String rarityId);
    List<String> getRarityIds();
}
