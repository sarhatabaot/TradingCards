package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigurate;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class PacksConfig extends SimpleConfigurate {
    public PacksConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "packs.yml", "settings");


    }

    public static class Pack {
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

    public static class PackSerializer implements TypeSerializer<Pack> {
        @Override
        public Pack deserialize(Type type, ConfigurationNode node) throws SerializationException {
            return null;
        }

        @Override
        public void serialize(Type type, @Nullable Pack obj, ConfigurationNode node) throws SerializationException {

        }
    }
}
