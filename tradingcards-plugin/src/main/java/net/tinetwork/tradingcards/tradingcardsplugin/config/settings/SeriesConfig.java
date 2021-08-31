package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.api.model.schedule.Schedule;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigurate;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;

/**
 * @author sarhatabaot
 */
public class SeriesConfig extends SimpleConfigurate {
    private String name;
    private String displayName;
    private Mode mode;
    private Schedule schedule;

    public SeriesConfig(final TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings"+ File.separator, "series.yml", "settings");
    }

    @Override
    protected void initValues() throws ConfigurateException {

    }

    @Override
    protected void preLoaderBuild() {

    }
}
