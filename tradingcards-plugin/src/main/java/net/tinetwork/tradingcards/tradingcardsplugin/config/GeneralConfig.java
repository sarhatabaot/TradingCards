package net.tinetwork.tradingcards.tradingcardsplugin.config;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfig;
import org.bukkit.Material;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Paths;

public class GeneralConfig extends SimpleConfig {
    private final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().
            path(Paths.get("settings/general",".yml")).build();
    private CommentedConfigurationNode rootNode;
    private final TradingCards plugin;

    private boolean debugMode;

    private Material cardMaterial;
    private String cardPrefix;
    private String shinyName;

    private boolean deckInCreative;
    private boolean useDeckItem;
    private boolean useLargeDecks;

    public GeneralConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "general.yml", "settings");
        this.plugin = plugin;
        this.rootNode = loader.load();

        this.debugMode = rootNode.node("debug-mode").getBoolean(false);
        this.cardPrefix = rootNode.node("card-prefix").getString("Card ");
        this.shinyName = rootNode.node("shiny-name").getString("Shiny");
    }

    public boolean debugMode() {
        return debugMode;
    }

    public String cardPrefix() {
        return cardPrefix;
    }

    public String shinyName() {
        return shinyName;
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
