package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.tinetwork.tradingcards.api.manager.SeriesManager;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalLog;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author sarhatabaot
 */
public class TradingSeriesManager implements SeriesManager {
    private final LoadingCache <String,Series> seriesCache;
    private final TradingCards plugin;
    private final List<String> seriesIds;

    public TradingSeriesManager(final TradingCards plugin) {
        this.plugin = plugin;
        this.seriesIds = getAllSeries().stream().map(Series::getId).toList();
        this.seriesCache = loadCache();
        preLoadCache();
        this.plugin.getLogger().info(() -> InternalLog.Init.LOAD_SERIES_MANAGER);
    }

    @Contract(" -> new")
    private @NotNull LoadingCache<String,Series> loadCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(50)
                .refreshAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public Series load(final String key) throws Exception {
                        plugin.debug(TradingCardManager.class, InternalDebug.LOADED_INTO_CACHE.formatted(key));
                        return plugin.getStorage().getSeries(key);
                    }
                });
    }

    private void preLoadCache() {
        try {
            this.seriesCache.getAll(seriesIds);
        } catch (ExecutionException e) {
            //ignored
        }
    }

    @Override
    public Series getSeries(final String seriesId) {
        return seriesCache.getUnchecked(seriesId);
    }

    @Override
    public Collection<Series> getAllSeries() {
        return seriesCache.asMap().values();
    }

    @Override
    public boolean containsSeries(final String seriesId) {
        return plugin.getStorage().getSeries(seriesId) != null;
    }

    public List<String> getSeriesIds() {
        return this.seriesIds;
    }
}
