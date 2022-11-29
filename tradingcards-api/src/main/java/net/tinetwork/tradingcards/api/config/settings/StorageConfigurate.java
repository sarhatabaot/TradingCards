package net.tinetwork.tradingcards.api.config.settings;

import com.github.sarhatabaot.kraken.core.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.api.TradingCardsPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;

/**
 * @author sarhatabaot
 */
public abstract class StorageConfigurate extends YamlConfigurateFile<TradingCardsPlugin<?>> {

    protected StorageConfigurate(@NotNull final TradingCardsPlugin<?> plugin, final String resourcePath, final String fileName, final String folder) throws ConfigurateException {
        super(plugin, resourcePath, fileName, folder);
    }

    public abstract String getAddress();

    public abstract int getPort();

    public abstract String getDatabase();

    public abstract String getUsername();

    public abstract String getTablePrefix();

    public abstract String getDefaultSeriesId();

    public abstract String getDefaultCardsFile();

    public abstract boolean isFirstTimeValues();
}
