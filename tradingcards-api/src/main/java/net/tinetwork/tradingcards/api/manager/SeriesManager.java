package net.tinetwork.tradingcards.api.manager;

import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.model.Series;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author sarhatabaot
 */
public interface SeriesManager {
    @Nullable
    Series getSeries(final String seriesId);
    Collection<Series> getAllSeries();
    boolean containsSeries(final String seriesId);
}
