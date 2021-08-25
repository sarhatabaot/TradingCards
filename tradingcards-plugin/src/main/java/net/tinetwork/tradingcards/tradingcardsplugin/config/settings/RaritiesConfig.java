package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigurate;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

public class RaritiesConfig extends SimpleConfigurate {
    private final List<Rarity> rarities = new ArrayList<>();
    private final ConfigurationNode raritiesNode;

    public RaritiesConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings"+ File.separator,"rarities.yml", "settings");
        loader.defaultOptions().serializers(builder -> builder.register(Rarity.class, RaritySerializer.INSTANCE));

        this.raritiesNode = rootNode.node("rarities");
        loadRarities();
    }

    public Rarity getRarity(final String id) throws SerializationException {
        return raritiesNode.node(id).get(Rarity.class);
    }

    private void loadRarities() throws SerializationException {
        for(Map.Entry<Object, ? extends ConfigurationNode> nodeEntry: raritiesNode.childrenMap().entrySet()) {
            rarities.add(getRarity(nodeEntry.getValue().key().toString()));
        }
    }

    public List<Rarity> rarities() {
        return rarities;
    }

    public static final class RaritySerializer implements TypeSerializer<Rarity> {
        public static final RaritySerializer INSTANCE = new RaritySerializer();
        private static final String NAME = "name";
        private static final String DISPLAY_NAME = "display-name";
        private static final String DEFAULT_COLOR = "default-color";
        private static final String REWARDS = "rewards";

        private RaritySerializer() {
        }

        private ConfigurationNode nonVirtualNode(final ConfigurationNode source, final Object... path) throws SerializationException {
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

            final String name = nameNode.getString();
            final String displayName = displayNameNode.getString();
            final String defaultColor = defaultColorNode.getString();
            final List<String> rewards = rewardsNode.getList(String.class);

            return new Rarity(name,displayName,defaultColor,rewards);
        }

        //Only implemented this since it's required. We don't actually use this feature yet.
        @Override
        public void serialize(Type type, @Nullable Rarity rarity, ConfigurationNode target) throws SerializationException {
            if(rarity == null) {
                target.raw(null);
                return;
            }

            target.node(NAME).set(rarity.getName());
            target.node(DISPLAY_NAME).set(rarity.getDisplayName());
            target.node(DEFAULT_COLOR).set(rarity.getDefaultColor());
            target.node(REWARDS).set(rarity.getRewards());
        }
    }


}
