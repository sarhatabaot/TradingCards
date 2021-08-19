package net.tinetwork.tradingcards.tradingcardsplugin.config;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfig;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Paths;
import java.util.List;

public class RaritiesConfig extends SimpleConfig {
    private final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().
            path(Paths.get("settings/rarities",".yml")).build();
    private CommentedConfigurationNode rootNode;
    private List<RarityEntry> rarities;

    public RaritiesConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "rarities.yml", "settings");

        this.rootNode = loader.load();
    }
    //TODO Serialize this using configurate.
    public class RarityEntry {
        private String name;
        private String displayName;
        private String defaultColor;
        private List<String> rewards;
    }

    @Override
    public void reloadConfig()  {
        try {
            this.rootNode = loader.load();
        } catch (ConfigurateException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }
}
