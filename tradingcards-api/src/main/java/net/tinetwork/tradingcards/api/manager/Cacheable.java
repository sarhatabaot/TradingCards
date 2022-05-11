package net.tinetwork.tradingcards.api.manager;

import com.google.common.cache.LoadingCache;

/**
 * @author sarhatabaot
 */
public interface Cacheable<T> {
    LoadingCache<String,T> loadCache();
    void preLoadCache();
}
