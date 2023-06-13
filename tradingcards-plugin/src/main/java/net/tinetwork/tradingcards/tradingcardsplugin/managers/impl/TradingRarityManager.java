package net.tinetwork.tradingcards.tradingcardsplugin.managers.impl;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.tinetwork.tradingcards.api.manager.Cacheable;
import net.tinetwork.tradingcards.api.manager.RarityManager;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.Manager;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalLog;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TradingRarityManager extends Manager<String,Rarity> implements RarityManager, Cacheable<String,Rarity> {
    private List<String> keys;
    public static final Rarity EMPTY_RARITY = new Rarity("empty","empty","",0.00,0.00, Collections.singletonList(""), null);
    public TradingRarityManager(final TradingCards plugin) {
        super(plugin);

        this.plugin.getLogger().info(() -> InternalLog.Init.LOAD_RARITY_MANAGER);
    }

    @Override
    public List<String> getKeys() {
        if(this.keys == null) {
            this.keys = getRarities().stream().map(Rarity::getId).toList();
        }
        return this.keys;
    }

    @Contract(" -> new")
    public @NotNull LoadingCache<String, Rarity> loadCache() {
        return Caffeine.newBuilder()
                .maximumSize(plugin.getAdvancedConfig().getRarity().maxCacheSize())
                .refreshAfterWrite(plugin.getAdvancedConfig().getRarity().refreshAfterWrite(), TimeUnit.MINUTES)
                .build(key -> {
                        plugin.debug(TradingRarityManager.class, InternalDebug.LOADED_INTO_CACHE.formatted(key));
                        return plugin.getStorage().getRarityById(key);
                    });
    }

    @Nullable
    public Rarity getRarity(final String id) {
        return this.cache.get(id);
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
        return getKeys();
    }

    public LoadingCache<String,Rarity> getRarityCache(){
        return this.cache;
    }
}
