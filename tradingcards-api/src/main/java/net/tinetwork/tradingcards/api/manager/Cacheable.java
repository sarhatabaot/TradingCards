package net.tinetwork.tradingcards.api.manager;


import com.github.benmanes.caffeine.cache.LoadingCache;

/**
 * @author sarhatabaot
 */
public interface Cacheable<R,T> {
    LoadingCache<R,T> loadCache();
    void preLoadCache();
    void forceCacheRefresh();
}
