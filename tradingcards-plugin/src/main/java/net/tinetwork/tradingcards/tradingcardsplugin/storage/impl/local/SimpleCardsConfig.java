package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local;

import net.tinetwork.tradingcards.api.TradingCardsPlugin;
import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.config.SimpleConfigurate;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.DropTypeManager;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
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
    private ConfigurationNode cardsNode;
    private static YamlStorage yamlStorage;

    public SimpleCardsConfig(final TradingCards plugin, final String fileName, final YamlStorage yamlStorage) throws ConfigurateException {
        super(plugin, "cards" + File.separator, fileName, "cards");
        SimpleCardsConfig.yamlStorage = yamlStorage;

    }

    @Override
    protected void preLoaderBuild() {
        CardSerializer.init(plugin);
        loaderBuilder.defaultOptions(opts -> opts.serializers(builder ->
                builder.registerExact(TradingCard.class, CardSerializer.INSTANCE).
                        registerExact(SeriesConfig.SeriesSerializer.TYPE, SeriesConfig.SeriesSerializer.INSTANCE)
                .registerExact(SeriesConfig.ColorSeriesSerializer.TYPE, SeriesConfig.ColorSeriesSerializer.INSTANCE))).build();
    }


    @Override
    protected void initValues() throws ConfigurateException {
        this.cardsNode = rootNode.node("cards");
    }

    public TradingCard getCard(final String rarity, final String name) {
        try {
            return cardsNode.node(rarity, name).get(TradingCard.class);
        } catch (SerializationException e) {
            Util.logSevereException(e);
            return new EmptyCard();
        }
    }


    public void createCard(final String cardId, final String rarityId, final String seriesId) {
        final Rarity rarity = plugin.getRarityManager().getRarity(rarityId);
        final Series series = plugin.getSeriesManager().getSeries(seriesId);
        ConfigurationNode rarityNode = cardsNode.node(rarityId);
        TradingCard card = new TradingCard(cardId).rarity(rarity).series(series).get();
        plugin.debug(SimpleCardsConfig.class, card.toString());
        try {
            rarityNode.node(cardId).set(card);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public void editDisplayName(final String rarityId, final String cardId,final String seriesId,final String displayName){
        ConfigurationNode rarityNode = cardsNode.node(rarityId);
        ConfigurationNode cardNode = rarityNode.node(cardId);
        try {
            TradingCard card = cardNode.get(TradingCard.class);
            if(card == null) {
                throw new ConfigurateException();
            }
            card.displayName(displayName);
            cardNode.set(card);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public void editSeries(final String rarityId, final String cardId,final String seriesId,final Series series) {
        ConfigurationNode rarityNode = cardsNode.node(rarityId);
        ConfigurationNode cardNode = rarityNode.node(cardId);
        try {
            TradingCard card = cardNode.get(TradingCard.class);
            if(card == null) {
                throw new ConfigurateException();
            }
            card.series(series);
            cardNode.set(card);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public void editSellPrice(final String rarityId, final String cardId,final String seriesId,final double sellPrice) {
        ConfigurationNode rarityNode = cardsNode.node(rarityId);
        ConfigurationNode cardNode = rarityNode.node(cardId);
        try {
            TradingCard card = cardNode.get(TradingCard.class);
            if(card == null) {
                throw new ConfigurateException();
            }
            card.sellPrice(sellPrice);
            cardNode.set(card);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }
    public void editType(final String rarityId, final String cardId,final String seriesId,final DropType type) {
        ConfigurationNode rarityNode = cardsNode.node(rarityId);
        ConfigurationNode cardNode = rarityNode.node(cardId);
        try {
            TradingCard card = cardNode.get(TradingCard.class);
            if(card == null) {
                throw new ConfigurateException();
            }
            card.type(type);
            cardNode.set(card);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public void editInfo(final String rarityId, final String cardId,final String seriesId,final String info) {
        ConfigurationNode rarityNode = cardsNode.node(rarityId);
        ConfigurationNode cardNode = rarityNode.node(cardId);
        try {
            TradingCard card = cardNode.get(TradingCard.class);
            if(card == null) {
                throw new ConfigurateException();
            }
            card.info(info);
            cardNode.set(card);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public void editModelData(final String rarityId, final String cardId,final String seriesId,final int data) {
        ConfigurationNode rarityNode = cardsNode.node(rarityId);
        ConfigurationNode cardNode = rarityNode.node(cardId);
        try {
            TradingCard card = cardNode.get(TradingCard.class);
            if(card == null) {
                throw new ConfigurateException();
            }
            card.customModelNbt(data);
            cardNode.set(card);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public void editBuyPrice(final String rarityId, final String cardId,final String seriesId,final double buyPrice) {
        ConfigurationNode rarityNode = cardsNode.node(rarityId);
        ConfigurationNode cardNode = rarityNode.node(cardId);
        try {
            TradingCard card = cardNode.get(TradingCard.class);
            if(card == null) {
                throw new ConfigurateException();
            }
            card.buyPrice(buyPrice);
            cardNode.set(card);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public static class CardSerializer implements TypeSerializer<TradingCard> {
        @SuppressWarnings("rawtypes")
        private static TradingCardsPlugin<? extends Card> plugin;
        public static CardSerializer INSTANCE = new CardSerializer();
        private static final String DISPLAY_NAME = "display-name";
        private static final String SERIES = "series";
        private static final String TYPE = "type";
        private static final String HAS_SHINY = "has-shiny-version";
        private static final String INFO = "info";
        private static final String ABOUT = "about";
        private static final String BUY_PRICE = "buy-price";
        private static final String SELL_PRICE = "sell-price";
        private static final String CUSTOM_MODEL_DATA = "custom-model-data";
        private static final String MATERIAL = "material";

        @SuppressWarnings("rawtypes")
        public static void init(TradingCardsPlugin<? extends Card> plugin) {
            CardSerializer.plugin = plugin;
        }

        private CardSerializer() {
        }

        @Override
        public TradingCard deserialize(Type type, @NotNull ConfigurationNode node) throws SerializationException {
            final String id = node.key().toString();
            final String rarityId = node.parent().key().toString();
            final String displayName = node.node(DISPLAY_NAME).getString();
            final String seriesId = node.node(SERIES).getString();
            final Material material = getMaterial(node.node(MATERIAL).getString());
            final DropType cardType = getDropType(node, id);
            final boolean hasShiny = node.node(HAS_SHINY).getBoolean();
            final String info = node.node(INFO).getString();
            final String about = node.node(ABOUT).getString();
            final int customModelData = node.node(CUSTOM_MODEL_DATA).getInt(0);

            final Rarity rarity = yamlStorage.getRaritiesConfig().getRarity(rarityId);
            final double buyPrice = getBuyPrice(node, rarity);
            final double sellPrice = getSellPrice(node, rarity);

            final Series series = yamlStorage.getSeriesConfig().series().get(seriesId);
            TradingCard card = new TradingCard(id);
            return card.material(material)
                    .rarity(rarity)
                    .displayName(displayName)
                    .series(series)
                    .type(cardType)
                    .hasShiny(hasShiny)
                    .info(info)
                    .about(about)
                    .buyPrice(buyPrice)
                    .sellPrice(sellPrice)
                    .customModelNbt(customModelData).get();
        }

        private double getBuyPrice(@NotNull ConfigurationNode node, Rarity rarity) {
            if(node.node(BUY_PRICE).isNull()) {
                return rarity.buyPrice();
            } else {
                return node.node(BUY_PRICE).getDouble(0.0D);
            }
        }

        private double getSellPrice(@NotNull ConfigurationNode node, Rarity rarity){
            if(node.node(SELL_PRICE).isNull()) {
                return rarity.sellPrice();
            } else {
                return node.node(SELL_PRICE).getDouble(0.0D);
            }
        }

        private Material getMaterial(final String materialName){
            if(materialName == null) {
                return plugin.getGeneralConfig().cardMaterial();
            } else {
                return Material.matchMaterial(materialName);
            }
        }

        @NotNull
        private DropType getDropType(final @NotNull ConfigurationNode node, final String id){
            try {
                final String typeId = node.node(TYPE).getString();

                if(typeId == null)
                    throw new NullPointerException();

                DropType dropType = yamlStorage.getCustomTypesConfig().getCustomType(typeId);
                if(dropType == null) {
                    dropType = plugin.getDropTypeManager().getDefaultTypes()
                            .stream()
                            .filter(type -> typeId.equals(type.getId())).toList().get(0);
                    if (dropType != null)
                        return dropType;
                    throw new NullPointerException();
                }
                return dropType;
            } catch (SerializationException|NullPointerException|ArrayIndexOutOfBoundsException e) {
                plugin.getLogger().warning("Could not get the type for " + id + " reason: " + e.getMessage());
                plugin.getLogger().warning("Defaulting to passive.");
                return DropTypeManager.PASSIVE;
            }
        }

        @Override
        public void serialize(Type type, @Nullable TradingCard card, ConfigurationNode target) throws SerializationException {
            if (card == null) {
                target.raw(null);
                return;
            }

            target.node(DISPLAY_NAME).set(card.getDisplayName());
            target.node(SERIES).set(card.getSeries());
            target.node(TYPE).set(card.getType());
            target.node(HAS_SHINY).set(card.hasShiny());
            target.node(INFO).set(card.getInfo());
            target.node(ABOUT).set(card.getAbout());
            target.node(BUY_PRICE).set(card.getBuyPrice());
            target.node(SELL_PRICE).set(card.getSellPrice());
            target.node(CUSTOM_MODEL_DATA).set(card.getCustomModelNbt());
        }
    }

    public Map<Object, ? extends ConfigurationNode> getRarities() {
        return cardsNode.childrenMap();
    }

    public Map<Object, ? extends ConfigurationNode> getCards(final String rarity) {
        return cardsNode.node(rarity).childrenMap();
    }

}
