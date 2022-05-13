package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import com.google.common.cache.LoadingCache;
import net.tinetwork.tradingcards.api.manager.Cacheable;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author sarhatabaot
 */
public abstract class Manager<R,T> implements Cacheable<R,T> {
    protected TradingCards plugin;
    protected LoadingCache<R,T> cache;

    protected Manager(final TradingCards plugin) {
        this.plugin = plugin;
        this.cache = loadCache();
        preLoadCache();
    }

    public void preLoadCache() {
        try {
            this.cache.getAll(getKeys());
        } catch (ExecutionException e){
            //ignored
        }
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
