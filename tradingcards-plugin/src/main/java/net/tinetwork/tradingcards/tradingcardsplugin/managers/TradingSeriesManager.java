package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import net.tinetwork.tradingcards.api.manager.SeriesManager;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;

import java.util.Collection;
import java.util.List;

/**
 * @author sarhatabaot
 */
public class TradingSeriesManager implements SeriesManager {
    private final TradingCards plugin;
    private final List<String> seriesIds;

    public TradingSeriesManager(final TradingCards plugin) {
        this.plugin = plugin;
        this.seriesIds = getAllSeries().stream().map(Series::getId).toList();
        this.plugin.getLogger().info(() -> "Loaded SeriesManager.");
    }

    @Override
    public Series getSeries(final String seriesId) {
        return plugin.getStorage().getSeries(seriesId);
    }

    @Override
    public Collection<Series> getAllSeries() {
        return plugin.getStorage().getAllSeries();
    }

    @Override
    public boolean containsSeries(final String seriesId) {
        return plugin.getStorage().getSeries(seriesId) != null;
    }

    public List<String> getSeriesIds() {
        return this.seriesIds;
    }
}
