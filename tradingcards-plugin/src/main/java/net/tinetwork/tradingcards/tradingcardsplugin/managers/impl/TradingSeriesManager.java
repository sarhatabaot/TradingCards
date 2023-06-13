package net.tinetwork.tradingcards.tradingcardsplugin.managers.impl;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.tinetwork.tradingcards.api.manager.SeriesManager;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.Manager;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalLog;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author sarhatabaot
 */
public class TradingSeriesManager extends Manager<String, Series> implements SeriesManager {

    public TradingSeriesManager(final TradingCards plugin) {
        super(plugin);
        this.plugin.getLogger().info(() -> InternalLog.Init.LOAD_SERIES_MANAGER);
    }

    @Contract(" -> new")
    public @NotNull LoadingCache<String, Series> loadCache() {
        return Caffeine.newBuilder()
                .maximumSize(plugin.getAdvancedConfig().getSeries().maxCacheSize())
                .refreshAfterWrite(plugin.getAdvancedConfig().getSeries().refreshAfterWrite(), TimeUnit.MINUTES)
                .build(key -> {
                    plugin.debug(TradingSeriesManager.class, InternalDebug.LOADED_INTO_CACHE.formatted(key));
                    return plugin.getStorage().getSeries(key);
                });
    }


    @Override
    public Series getSeries(final String seriesId) {
        return cache.get(seriesId);
    }

    @Override
    public Collection<Series> getAllSeries() {
        return cache.asMap().values();
    }

    @Override
    public boolean containsSeries(final String seriesId) {
        return plugin.getStorage().getSeries(seriesId) != null;
    }

    public List<String> getSeriesIds() {
        return getKeys();
    }

    @Override
    public List<String> getKeys() {
        return plugin.getStorage().getAllSeries().stream().map(Series::getId).toList();
    }
}
