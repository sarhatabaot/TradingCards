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

        public Pack(int numNormalCards, String normalCardRarity, int numSpecialCards, String specialCardsRarity, int numExtraCards, String extraCardsRarity, String series, double price, String permissions) {
            this.numNormalCards = numNormalCards;
            this.normalCardRarity = normalCardRarity;
            this.numSpecialCards = numSpecialCards;
            this.specialCardsRarity = specialCardsRarity;
            this.numExtraCards = numExtraCards;
            this.extraCardsRarity = extraCardsRarity;
            this.series = series;
            this.price = price;
            this.permissions = permissions;
        }

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
            final ConfigurationNode numNormalCardsNode = node.node(NUM_NORMAL_CARDS);
            final ConfigurationNode normalCardRarityNode = node.node(NORMAL_CARDS_RARITY);
            final ConfigurationNode numExtraCardsNode = node.node(NUM_EXTRA_CARDS);
            final ConfigurationNode extraCardRarityNode = node.node(EXTRA_CARDS_RARITY);
            final ConfigurationNode numSpecialCardsNode = node.node(NUM_SPECIAL_CARDS);
            final ConfigurationNode specialCardRarityNode = node.node(SPECIAL_CARD_RARITY);
            final ConfigurationNode seriesNode = node.node(SERIES);
            final ConfigurationNode priceNode = node.node(PRICE);
            final ConfigurationNode permissionsNode = node.node(PERMISSION);

            final int numNormalCards = numNormalCardsNode.getInt(0);
            final String normalCardRarity = normalCardRarityNode.getString();
            final int numExtraCards = numExtraCardsNode.getInt(0);
            final String extraCardRarity = extraCardRarityNode.getString();
            final int numSpecialCards = numSpecialCardsNode.getInt(0);
            final String specialCardRarity = specialCardRarityNode.getString();
            final String series = seriesNode.getString();
            final double price = priceNode.getDouble(0.0D);
            final String permissions = permissionsNode.getString();
            return new Pack(numNormalCards,normalCardRarity,numSpecialCards,specialCardRarity,numExtraCards,extraCardRarity,series,price,permissions);
        }

        //Only implemented this since it's required. We don't actually use this feature yet.
        @Override
        public void serialize(Type type, @Nullable Pack pack, ConfigurationNode target) throws SerializationException {
            if(pack == null) {
                target.raw(null);
                return;
            }

            target.node(NUM_NORMAL_CARDS).set(pack.getNumNormalCards());
            target.node(NORMAL_CARDS_RARITY).set(pack.getNormalCardRarity());
            target.node(NUM_EXTRA_CARDS).set(pack.getNumExtraCards());
            target.node(EXTRA_CARDS_RARITY).set(pack.getExtraCardsRarity());
            target.node(NUM_SPECIAL_CARDS).set(pack.getNumSpecialCards());
            target.node(SPECIAL_CARD_RARITY).set(pack.specialCardsRarity);
            target.node(SERIES).set(pack.getSeries());
            target.node(PRICE).set(pack.getPrice());
            target.node(PERMISSION).set(pack.getPermissions());
        }
    }
}
