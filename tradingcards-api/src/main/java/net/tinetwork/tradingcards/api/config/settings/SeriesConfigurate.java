package net.tinetwork.tradingcards.api.config.settings;

import com.github.sarhatabaot.kraken.core.config.ConfigurateFile;
import net.tinetwork.tradingcards.api.TradingCardsPlugin;
import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.model.Series;
import org.spongepowered.configurate.ConfigurateException;

import java.util.Map;

/**
 * @author sarhatabaot
 */
public abstract class SeriesConfigurate extends ConfigurateFile<TradingCardsPlugin<?>> {

    public SeriesConfigurate(final TradingCardsPlugin<? extends Card<?>> plugin, final String resourcePath, final String fileName, final String folder) throws ConfigurateException {
        super(plugin, resourcePath, fileName, folder);
    }

    public abstract Map<String, Series> series();
}
