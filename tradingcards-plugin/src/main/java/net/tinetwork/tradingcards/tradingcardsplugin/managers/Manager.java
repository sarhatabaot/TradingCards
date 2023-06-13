package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import com.github.benmanes.caffeine.cache.LoadingCache;

import net.tinetwork.tradingcards.api.manager.Cacheable;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;

import java.util.List;

/**
 * @author sarhatabaot
 */
public abstract class Manager<R,T> implements Cacheable<R,T> {
    protected final TradingCards plugin;
    protected final LoadingCache<R,T> cache;

    protected Manager(final TradingCards plugin) {
        this.plugin = plugin;
        this.cache = loadCache();
        preLoadCache();
    }

    public void preLoadCache() {
        this.cache.getAll(getKeys());
    }

    @Override
    public void forceCacheRefresh() {
        this.cache.invalidateAll();
        preLoadCache();
    }

    public abstract List<R> getKeys();

    public LoadingCache<R, T> getCache() {
        return cache;
    }


}
