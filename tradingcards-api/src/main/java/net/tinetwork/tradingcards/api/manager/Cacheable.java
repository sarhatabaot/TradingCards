package net.tinetwork.tradingcards.api.manager;

import com.google.common.cache.LoadingCache;

/**
 * @author sarhatabaot
 */
public interface Cacheable<R,T> {
    LoadingCache<R,T> loadCache();
    void preLoadCache();
}
