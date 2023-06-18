package com.github.sarhatabaot.tradingcards.managers

import com.github.benmanes.caffeine.cache.LoadingCache
import net.tinetwork.tradingcards.api.manager.Cacheable
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards

/**
 *
 * @author sarhatabaot
 */

abstract class Manager<R, T> protected constructor(protected var plugin: TradingCards) :
    Cacheable<R, T> {
    protected val cache: LoadingCache<R, T> by lazy { loadCache() }

    init {
        preLoadCache()
    }

    override fun preLoadCache() {
        cache.getAll(getKeys())
    }

    override fun forceCacheRefresh() {
        cache.invalidateAll()
        preLoadCache()
    }

    abstract fun getKeys(): List<R>?
    fun getCache(): LoadingCache<R, T> {
        return cache
    }
}
