package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local;

import com.github.sarhatabaot.kraken.core.config.Transformation;
import net.tinetwork.tradingcards.api.config.settings.RarityConfigurate;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RaritiesConfig extends RarityConfigurate{
    private List<Rarity> rarities;
    private CommentedConfigurationNode raritiesNode;

    public RaritiesConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "data"+ File.separator,"rarities.yml", "data");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.raritiesNode = rootNode.node("rarities");
        loadRarities();
    }

    @Override
    protected void builderOptions() {
        loaderBuilder.defaultOptions(opts -> opts.serializers(builder ->
                builder.registerExact(RaritySerializer.TYPE, RaritySerializer.INSTANCE)));
    }

    @Override
    protected Transformation getTransformation() {
        return null;
    }

    public Rarity getRarity(final String id) throws SerializationException {
        return raritiesNode.node(id).get(Rarity.class);
    }

    public boolean containsRarity(final String id) {
        return raritiesNode.hasChild(id);
    }

    public void editBuyPrice(final String rarityId, final double buyPrice){
        ConfigurationNode rarityNode = raritiesNode.node(rarityId);
        try {
            Rarity rarity = getRarity(rarityId);
            rarity.setBuyPrice(buyPrice);
            rarityNode.set(rarity);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e){
            Util.logSevereException(e);
        }
    }

    public void editAddReward(final String rarityId,final String reward){
        ConfigurationNode rarityNode = raritiesNode.node(rarityId);
        try {
            Rarity rarity = getRarity(rarityId);
            rarity.getRewards().add(reward);
            rarityNode.set(rarity);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e){
            Util.logSevereException(e);
        }
    }

    public void editDefaultColor(final String rarityId, final String defaultColor) {
        ConfigurationNode rarityNode = raritiesNode.node(rarityId);
        try {
            Rarity rarity = getRarity(rarityId);
            rarity.setDefaultColor(defaultColor);
            rarityNode.set(rarity);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e){
            Util.logSevereException(e);
        }
    }

    public void editDisplayName(final String rarityId, final String displayName) {
        ConfigurationNode rarityNode = raritiesNode.node(rarityId);
        try {
            Rarity rarity = getRarity(rarityId);
            rarity.setDisplayName(displayName);
            rarityNode.set(rarity);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e){
            Util.logSevereException(e);
        }
    }

    public void editSellPrice(final String rarityId, final double sellPrice) {
        ConfigurationNode rarityNode = raritiesNode.node(rarityId);
        try {
            Rarity rarity = getRarity(rarityId);
            rarity.setSellPrice(sellPrice);
            rarityNode.set(rarity);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e){
            Util.logSevereException(e);
        }
    }

    public void editRemoveAllRewards(final String rarityId) {
        ConfigurationNode rarityNode = raritiesNode.node(rarityId);
        try {
            Rarity rarity = getRarity(rarityId);
            rarity.setRewards(new ArrayList<>());
            rarityNode.set(rarity);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e){
            Util.logSevereException(e);
        }
    }

    public void editRemoveReward(final String rarityId, final int rewardNumber) {
        ConfigurationNode rarityNode = raritiesNode.node(rarityId);
        try {
            Rarity rarity = getRarity(rarityId);
            rarity.getRewards().remove(rewardNumber);
            rarityNode.set(rarity);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e){
            Util.logSevereException(e);
        }
    }

    private void loadRarities()  {
        this.rarities = new ArrayList<>();
        for(Map.Entry<Object, ? extends ConfigurationNode> nodeEntry: raritiesNode.childrenMap().entrySet()) {
            final String rarityKey = nodeEntry.getValue().key().toString();
            try {
                rarities.add(getRarity(rarityKey));
            } catch (SerializationException e){
                plugin.getLogger().severe(e.getMessage());
                plugin.debug(RaritiesConfig.class,"Couldn't add="+rarityKey);
            }
        }
        plugin.debug(RaritiesConfig.class,"Total Rarities="+rarities.size());
    }

    public List<Rarity> rarities() {
        return rarities;
    }

    public void createRarity(final String rarityId) {
        try {
            Rarity rarity = new Rarity(rarityId,rarityId,"",0D,0D,new ArrayList<>(),null);
            raritiesNode.node(rarityId).set(rarity);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public static final class RaritySerializer implements TypeSerializer<Rarity> {
        public static final RaritySerializer INSTANCE = new RaritySerializer();
        public static final Class<Rarity> TYPE = Rarity.class;
        private static final String NAME = "name";
        private static final String DISPLAY_NAME = "display-name";
        private static final String DEFAULT_COLOR = "default-color";
        private static final String REWARDS = "rewards";
        private static final String BUY_PRICE = "buy-price";
        private static final String SELL_PRICE = "sell-price";
        private static final String CURRENCY_ID = "currency-id";

        private RaritySerializer() {
        }

        private ConfigurationNode nonVirtualNode(final @NotNull ConfigurationNode source, final Object... path) throws SerializationException {
            if (!source.hasChild(path)) {
                throw new SerializationException("Required field " + Arrays.toString(path) + " was not present in node");
            }
            return source.node(path);
        }

        @Override
        public Rarity deserialize(Type type, ConfigurationNode node) throws SerializationException {
            final ConfigurationNode nameNode = nonVirtualNode(node, NAME);
            final ConfigurationNode displayNameNode = nonVirtualNode(node, DISPLAY_NAME);
            final ConfigurationNode defaultColorNode = nonVirtualNode(node, DEFAULT_COLOR);
            final ConfigurationNode rewardsNode = nonVirtualNode(node, REWARDS);
            final ConfigurationNode buyNode = node.node(BUY_PRICE);
            final ConfigurationNode sellNode = node.node(SELL_PRICE);

            final String name = nameNode.getString();
            final String displayName = displayNameNode.getString();
            final String defaultColor = defaultColorNode.getString();
            final List<String> rewards = rewardsNode.getList(String.class);
            final double buyPrice = buyNode.getDouble(0.0D);
            final double sellPrice = sellNode.getDouble(0.0D);
            final String currencyId = node.node(CURRENCY_ID).getString();

            return new Rarity(name,displayName,defaultColor,buyPrice,sellPrice,rewards,currencyId);
        }

        //Only implemented this since it's required. We don't actually use this feature yet.
        @Override
        public void serialize(Type type, Rarity rarity, ConfigurationNode target) throws SerializationException {
            if(rarity == null) {
                target.raw(null);
                return;
            }

            target.node(NAME).set(rarity.getId());
            target.node(DISPLAY_NAME).set(rarity.getDisplayName());
            target.node(DEFAULT_COLOR).set(rarity.getDefaultColor());
            target.node(REWARDS).set(rarity.getRewards());
            target.node(BUY_PRICE).set(rarity.getBuyPrice());
            target.node(SELL_PRICE).set(rarity.getSellPrice());
        }
    }

}
