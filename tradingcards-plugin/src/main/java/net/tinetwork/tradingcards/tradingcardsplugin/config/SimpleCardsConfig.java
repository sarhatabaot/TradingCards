package net.tinetwork.tradingcards.tradingcardsplugin.config;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.NullCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigurate;
import org.bukkit.configuration.ConfigurationSection;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author sarhatabaot
 */
public class SimpleCardsConfig extends SimpleConfigurate {
    private final ConfigurationNode cardsNode;
    @Deprecated
    private ConfigurationSection cards;
    public SimpleCardsConfig(final TradingCards plugin, final String fileName) throws ConfigurateException {
        super(plugin, "cards"+File.separator, fileName, "cards");

        this.cardsNode = rootNode.node("cards");
        //this.cards = getConfig().getConfigurationSection("cards");
        reloadConfig();
        plugin.debug("Created: "+fileName);
    }

    @Override
    protected void registerTypeSerializer() {
        loaderBuilder.defaultOptions(opts -> opts.serializers(builder ->
                builder.registerExact(TradingCard.class, CardSerializer.INSTANCE))).build();
    }

    public TradingCard getCard(final String rarity, final String name) {
        try {
            return cardsNode.node(rarity, name).get(TradingCard.class);
        } catch (SerializationException e) {
            plugin.getLogger().severe(e.getMessage());
            return new NullCard(plugin);
        }
    }

    public String series(final String rarity, final String name) {
        return cardsNode.node(rarity,name).node("series").getString("");
    }

    public String about(final String rarity, final String name) {
        return cardsNode.node(rarity,name).node("about").getString("");
    }

    public String info(final String rarity, final String name) {
        return cardsNode.node(rarity,name).node("info").getString("");
    }
    public String type(final String rarity, final String name) {
        return cardsNode.node(rarity,name).node("type").getString("");
    }

    public double buyPrice(final String rarity, final String name) {
        return cardsNode.node(rarity,name).node("buy-price").getDouble(0.0D);
    }
    public double sellPrice(final String rarity, final String name) {
        return cardsNode.node(rarity,name).node("sell-price").getDouble(0.0D);
    }

    public boolean hasShinyVersion(final String rarity, final String name) {
        return cardsNode.node(rarity,name).node("has-shiny-version").getBoolean(false);
    }

    public int customModelData(final String rarity, final String name) {
        return cardsNode.node(rarity,name).node("custom-model-data").getInt(0);
    }

    public String displayName(final String rarity, final String name) {
        return cardsNode.node(rarity,name).node("display-name").getString(name);
    }

    public static class CardSerializer implements TypeSerializer<TradingCard> {
        public static CardSerializer INSTANCE;
        private static final String DISPLAY_NAME = "display-name";
        private static final String SERIES = "series";
        private static final String TYPE = "type";
        private static final String HAS_SHINY = "has-shiny-version";
        private static final String INFO = "info";
        private static final String ABOUT = "about";
        private static final String BUY_PRICE = "buy-price";
        private static final String SELL_PRICE = "sell-price";
        private static final String CUSTOM_MODEL_DATA = "custom-model-data";

        private TradingCards plugin;

        public CardSerializer(TradingCards plugin) {
            this.plugin = plugin;
            CardSerializer.INSTANCE = new CardSerializer(plugin);
        }

        @Override
        public TradingCard deserialize(Type type, ConfigurationNode node) throws SerializationException {
            final String id = node.key().toString();
            final String rarity = node.parent().key().toString();
            final String displayName = node.node(DISPLAY_NAME).getString();
            final String series = node.node(SERIES).getString();
            final String cardType = node.node(TYPE).getString();
            final boolean hasShiny  = node.node(HAS_SHINY).getBoolean();
            final String info = node.node(INFO).getString();
            final String about = node.node(ABOUT).getString();
            final double buyPrice  = node.node(BUY_PRICE).getDouble(0.0D);
            final double sellPrice = node.node(SELL_PRICE).getDouble(0.0D);
            final int customModelData = node.node(CUSTOM_MODEL_DATA).getInt(0);

            TradingCard card = new TradingCard(plugin,id);
            return card.rarity(rarity)
                    .displayName(displayName)
                    .series(series)
                    .type(cardType)
                    .isShiny(hasShiny)
                    .info(info)
                    .about(about)
                    .buyPrice(buyPrice)
                    .sellPrice(sellPrice)
                    .customModelNbt(customModelData).get();
        }

        @Override
        public void serialize(Type type, @Nullable TradingCard obj, ConfigurationNode node) throws SerializationException {
            //todo
        }
    }


    public ConfigurationSection getCards() {
        return cards;
    }

    public Map<Object, ? extends ConfigurationNode> getRarities() {
        return cardsNode.childrenMap();
    }

    public Map<Object, ? extends ConfigurationNode> getCards(final String rarity) {
        return cardsNode.node(rarity).childrenMap();
    }

}
