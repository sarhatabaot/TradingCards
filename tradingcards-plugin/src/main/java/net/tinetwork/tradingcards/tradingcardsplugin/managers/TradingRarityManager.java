package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.tinetwork.tradingcards.api.manager.RarityManager;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TradingRarityManager implements RarityManager {
    private final LoadingCache<String,Rarity> rarityCache;
    private final TradingCards plugin;
    private final List<String> rarityIds;
    public static final Rarity EMPTY_RARITY = new Rarity("empty","empty","",0.00,0.00, Collections.singletonList(""));
    public TradingRarityManager(final TradingCards plugin) {
        this.plugin = plugin;
        this.rarityIds = getRarities().stream().map(Rarity::getId).toList();

        this.rarityCache = loadCache();
        preLoadCache();
    }
    @Contract(" -> new")
    private @NotNull LoadingCache<String, Rarity> loadCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(50)
                .refreshAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public Rarity load(final String key) {
                        plugin.debug(TradingCardManager.class,"Loaded into cache for "+key);
                        return plugin.getStorage().getRarityById(key);
                    }
                });
    }

    private void preLoadCache() {
        try {
            this.rarityCache.getAll(rarityIds);
        } catch (ExecutionException e){
            //ignored.
        }
    }
    @Nullable
    public Rarity getRarity(final String id) {
        return this.rarityCache.getUnchecked(id);
    }

    @Override
    public List<Rarity> getRarities() {
        return plugin.getStorage().getRarities();
    }

    @Override
    public boolean containsRarity(final String rarityId) {
        for(Rarity rarity: getRarities()) {
            if(rarity.getId().equals(rarityId))
                return true;
        }
        return false;
    }

    public List<String> getRarityIds() {
        return rarityIds;
    }

    public LoadingCache<String,Rarity> getRarityCache(){
        return this.rarityCache;
    }
}
