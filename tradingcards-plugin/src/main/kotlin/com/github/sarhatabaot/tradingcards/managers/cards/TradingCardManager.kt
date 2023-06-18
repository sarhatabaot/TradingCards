package com.github.sarhatabaot.tradingcards.managers.cards

import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.github.sarhatabaot.tradingcards.card.TradingCard
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards
import net.tinetwork.tradingcards.tradingcardsplugin.managers.Manager
import net.tinetwork.tradingcards.tradingcardsplugin.managers.cards.CompositeCardKey
import net.tinetwork.tradingcards.tradingcardsplugin.managers.cards.CompositeRaritySeriesKey
import org.jetbrains.annotations.Contract
import java.util.*
import java.util.concurrent.TimeUnit

/**
 *
 * @author sarhatabaot
 */

abstract class TradingCardManager protected constructor(plugin: TradingCards) :
    Manager<CompositeCardKey, TradingCard>(plugin) {
    protected val rarityCardCache: LoadingCache<String, List<TradingCard>> by lazy { loadRarityCardCache() }
    protected val seriesCardCache: LoadingCache<String, List<TradingCard>> by lazy { loadSeriesCardCache() }
    protected val rarityAndSeriesCardCache: LoadingCache<CompositeRaritySeriesKey, List<TradingCard>>

    init {
        preLoadRarityCache()
        preLoadSeriesCache()
        rarityAndSeriesCardCache = loadRarityAndSeriesCardCache()
        preLoadRarityAndSeriesCache()
    }

    override fun loadCache(): LoadingCache<CompositeCardKey, TradingCard> {
        return Caffeine.newBuilder()
            .maximumSize(plugin.advancedConfig.cards.maxCacheSize.toLong())
            .refreshAfterWrite(plugin.advancedConfig.cards.refreshAfterWrite.toLong(), TimeUnit.MINUTES)
            .build(
                CacheLoader { key: CompositeCardKey ->
                    plugin.debug(TradingCardManager::class.java, "Loaded Card=$key into cache.")
                    plugin.storage.getCard(key.cardId, key.rarityId, key.seriesId).get()
                }
            )
    }

    @Contract(" -> new")
    private fun loadSeriesCardCache(): LoadingCache<String, List<TradingCard>> {
        return Caffeine.newBuilder()
            .maximumSize(plugin.advancedConfig.cards.maxCacheSize.toLong())
            .refreshAfterWrite(plugin.advancedConfig.cards.refreshAfterWrite.toLong(), TimeUnit.MINUTES)
            .build { key: String? ->
                plugin.storage.getCardsInSeries(key)
            }
    }

    private fun loadRarityAndSeriesCardCache(): LoadingCache<CompositeRaritySeriesKey, List<TradingCard>> {
        return Caffeine.newBuilder()
            .maximumSize(plugin.advancedConfig.cards.maxCacheSize.toLong())
            .refreshAfterWrite(plugin.advancedConfig.cards.refreshAfterWrite.toLong(), TimeUnit.MINUTES)
            .build { key: CompositeRaritySeriesKey ->
                val cardList: List<TradingCard>? =
                    plugin.storage.getCardsInRarityAndSeries(key.rarityId, key.seriesId)
                Objects.requireNonNullElse(
                    cardList,
                    emptyList<Any>()
                )
            }
    }

    private fun preLoadRarityAndSeriesCache() {
        rarityAndSeriesCardCache.getAll(loadRaritySeriesKeys())
    }

    private fun loadRaritySeriesKeys(): List<CompositeRaritySeriesKey> {
        val keyList: MutableList<CompositeRaritySeriesKey> = ArrayList()
        for (seriesId in seriesCardCache.asMap().keys) {
            for (rarityId in rarityCardCache.asMap().keys) {
                keyList.add(CompositeRaritySeriesKey(rarityId, seriesId))
            }
        }
        return keyList
    }

    protected abstract fun loadRarityCardCache(): LoadingCache<String, List<TradingCard>>
    abstract fun preLoadRarityCache()
    abstract fun preLoadSeriesCache()
    fun getRarityCardCache(): LoadingCache<String, List<TradingCard>> {
        return rarityCardCache
    }

    fun getSeriesCardCache(): LoadingCache<String, List<TradingCard>> {
        return seriesCardCache
    }

    fun getRarityAndSeriesCardCache(): LoadingCache<CompositeRaritySeriesKey, List<TradingCard>?> {
        return rarityAndSeriesCardCache
    }
}
