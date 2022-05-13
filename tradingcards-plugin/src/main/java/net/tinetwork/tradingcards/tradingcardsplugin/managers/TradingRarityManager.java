package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.tinetwork.tradingcards.api.manager.Cacheable;
import net.tinetwork.tradingcards.api.manager.RarityManager;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.cards.AllCardManager;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalLog;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TradingRarityManager extends Manager<String,Rarity> implements RarityManager, Cacheable<String,Rarity> {
    public static final Rarity EMPTY_RARITY = new Rarity("empty","empty","",0.00,0.00, Collections.singletonList(""));
    public TradingRarityManager(final TradingCards plugin) {
        super(plugin);

        this.plugin.getLogger().info(() -> InternalLog.Init.LOAD_RARITY_MANAGER);
    }

    @Override
    public List<String> getKeys() {
        return getRarities().stream().map(Rarity::getId).toList();
    }

    @Contract(" -> new")
    public @NotNull LoadingCache<String, Rarity> loadCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(50)
                .refreshAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public Rarity load(final String key) {
                        plugin.debug(AllCardManager.class, InternalDebug.LOADED_INTO_CACHE.formatted(key));
                        return plugin.getStorage().getRarityById(key);
                    }
                });
    }

    @Nullable
    public Rarity getRarity(final String id) {
        return this.cache.getUnchecked(id);
    }

    @Override
    public Set<Rarity> getRarities() {
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
