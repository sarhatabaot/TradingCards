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
        loader.defaultOptions().serializers(builder -> builder.register(Pack.class, PackSerializer.INSTANCE));
    }

    public static class Pack {
        private int numNormalCards;
        private String normalCardRarity;
        private int numSpecialCards;
        private String specialCardsRarity;
        private int numExtraCards;
        private String extraCardsRarity;
        private String series;
        private double price;
        private String permissions;

        public int getNumNormalCards() {
            return numNormalCards;
        }

        public String getNormalCardRarity() {
            return normalCardRarity;
        }

        public int getNumSpecialCards() {
            return numSpecialCards;
        }


        public String getSpecialCardsRarity() {
            return specialCardsRarity;
        }

        public int getNumExtraCards() {
            return numExtraCards;
        }

        public String getExtraCardsRarity() {
            return extraCardsRarity;
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
        public static final PackSerializer INSTANCE = new PackSerializer();
        private static final String NUM_NORMAL_CARDS = "num-normal-cards";
        private static final String NORMAL_CARDS_RARITY = "normal-card-rarity";
        private static final String NUM_EXTRA_CARDS = "num-extra-cards";
        private static final String EXTRA_CARDS_RARITY = "extra-card-rarity";
        private static final String NUM_SPECIAL_CARDS = "num-special-cards";
        private static final String SPECIAL_CARD_RARITY = "special-card-rarity";
        private static final String SERIES = "series";
        private static final String PRICE = "prices";
        private static final String PERMISSION = "permission";
        private PackSerializer() {
        }
        @Override
        public Pack deserialize(Type type, ConfigurationNode node) throws SerializationException {
            return null;
        }

        @Override
        public void serialize(Type type, @Nullable Pack obj, ConfigurationNode node) throws SerializationException {

        }
    }
}
