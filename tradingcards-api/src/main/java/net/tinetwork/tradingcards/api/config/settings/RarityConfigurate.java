package net.tinetwork.tradingcards.api.config.settings;


import com.lapzupi.dev.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.api.TradingCardsPlugin;
import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.model.Rarity;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

/**
 * @author sarhatabaot
 */
public abstract class RarityConfigurate extends YamlConfigurateFile<TradingCardsPlugin<?>> {

    protected RarityConfigurate(final TradingCardsPlugin<? extends Card<?>> plugin, final String resourcePath, final String fileName, final String folder) throws ConfigurateException {
        super(plugin, resourcePath, fileName, folder);
    }

    public abstract Rarity getRarity(final String rarityId) throws SerializationException;
}
