package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigurate;
import org.spongepowered.configurate.ConfigurateException;

import java.util.List;

public class RaritiesConfig extends SimpleConfigurate {

    private List<RarityEntry> rarities;

    public RaritiesConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "rarities.yml", "settings");

    }
    //TODO Serialize this using configurate.
    public class RarityEntry {
        private String name;
        private String displayName;
        private String defaultColor;
        private List<String> rewards;
    }


}
