package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigurate;
import org.spongepowered.configurate.ConfigurateException;

public class PacksConfig extends SimpleConfigurate {
    public PacksConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "packs.yml", "settings");


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
