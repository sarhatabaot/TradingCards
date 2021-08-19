package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfig;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Paths;

public class PacksConfig extends SimpleConfig {
    private final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().
            path(Paths.get("settings/packs",".yml")).build();
    private CommentedConfigurationNode rootNode;

    public PacksConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "packs.yml", "settings");

        this.rootNode = loader.load();
    }

    //TODO, anything named entry is temporary, this should be renamed to just the item
    public class PackEntry {
        private int numNormalCards;
        private int normalCardRarity;
        private int numSpecialCards;
        private int specialCardRarity;
        private String series;
        private double price;
        private String permissions;

        public int getNumNormalCards() {
            return numNormalCards;
        }

        public int getNormalCardRarity() {
            return normalCardRarity;
        }

        public int getNumSpecialCards() {
            return numSpecialCards;
        }

        public int getSpecialCardRarity() {
            return specialCardRarity;
        }

        public String getSeries() {
            return series;
        }

        public double getPrice() {
            return price;
        }

        public String getPermissions() {
            return permissions;
        }
    }
}
