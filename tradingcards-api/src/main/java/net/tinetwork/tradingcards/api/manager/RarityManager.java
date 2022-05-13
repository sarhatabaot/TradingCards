package net.tinetwork.tradingcards.api.manager;

import com.google.common.cache.LoadingCache;
import net.tinetwork.tradingcards.api.model.Rarity;

import java.util.List;
import java.util.Set;

/**
 * @author sarhatabaot
 */
public interface RarityManager {
    Rarity getRarity(final String rarityId);
    Set<Rarity> getRarities();
    boolean containsRarity(final String rarityId);

    /**
     * @return returns an ordered list of rarityIds.
     */
    List<String> getRarityIds();
    LoadingCache<String,Rarity> getRarityCache();
}
