package net.tinetwork.tradingcards.api.manager;

import com.github.benmanes.caffeine.cache.LoadingCache;
import net.tinetwork.tradingcards.api.model.Rarity;

import java.util.List;

/**
 * @author sarhatabaot
 */
public interface RarityManager {
    Rarity getRarity(final String rarityId);
    List<Rarity> getRarities();
    boolean containsRarity(final String rarityId);

    /**
     * @return returns an ordered list of rarityIds.
     */
    List<String> getRarityIds();
    LoadingCache<String,Rarity> getRarityCache();
}
