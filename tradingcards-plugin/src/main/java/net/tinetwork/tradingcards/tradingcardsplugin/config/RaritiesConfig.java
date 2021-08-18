package net.tinetwork.tradingcards.tradingcardsplugin.config;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfig;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Paths;
import java.util.List;

public class RaritiesConfig extends SimpleConfig {
    private final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().
            path(Paths.get("settings/rarities",".yml")).build();
    private CommentedConfigurationNode rootNode;
    private List<Rarity> rarities;

    public RaritiesConfig(TradingCards plugin) {
        super(plugin, "rarities.yml", "settings");
    }

    public class Rarity {
        private String name;
        private String displayName;
        private String defaultColor;
        private List<String> rewards;
    }
}
