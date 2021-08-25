package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.api.model.Chance;
import net.tinetwork.tradingcards.api.model.EmptyChance;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigurate;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class ChancesConfig extends SimpleConfigurate {
    private List<String> raritiesId; //TODO not sure if we even need this

    private int hostileChance;
    private int neutralChance;
    private int passiveChance;
    private int bossChance;
    private boolean bossDrop;
    private int bossDropRarity;
    private int shinyVersionChance;

    public ChancesConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings" + File.separator,"chances.yml", "settings");
        loader.defaultOptions().serializers(builder -> builder.register(Chance.class, ChanceSerializer.INSTANCE));

        this.hostileChance = rootNode.node("hostile-chance").getInt(20000);
        this.neutralChance = rootNode.node("neutral-chance").getInt(5000);
        this.passiveChance = rootNode.node("passive-chance").getInt(1000);
        this.bossChance = rootNode.node("boss-chance").getInt(100000);
        this.bossDrop = rootNode.node("boss-drop").getBoolean(false);
        this.bossDropRarity = rootNode.node("boss-drop-rarity").getInt(5000);
        this.shinyVersionChance = rootNode.node("shiny-version-chance").getInt(1000);
    }

    public int hostileChance() {
        return hostileChance;
    }

    public int neutralChance() {
        return neutralChance;
    }

    public int passiveChance() {
        return passiveChance;
    }

    public int bossChance() {
        return bossChance;
    }

    public boolean bossDrop() {
        return bossDrop;
    }

    public int bossDropRarity() {
        return bossDropRarity;
    }

    public int shinyVersionChance() {
        return shinyVersionChance;
    }

    public Chance getChance(final String rarityId) {
        try {
            return rootNode.node(rarityId).get(Chance.class);
        } catch (SerializationException e){
            plugin.getLogger().severe(e.getMessage());
        }
        return new EmptyChance();
    }

    public static final class ChanceSerializer implements TypeSerializer<Chance> {
        private static final String HOSTILE = "hostile";
        private static final String NEUTRAL = "neutral";
        private static final String PASSIVE = "passive";
        private static final String BOSS = "boss";
        private static final int MAX_CHANCE = 100000;
        public static final ChanceSerializer INSTANCE = new ChanceSerializer();

        private ConfigurationNode nonVirtualNode(final ConfigurationNode source, final Object... path) throws SerializationException {
            if (!source.hasChild(path)) {
                throw new SerializationException("Required field " + Arrays.toString(path) + " was not present in node");
            }
            return source.node(path);
        }

        @Override
        public Chance deserialize(Type type, ConfigurationNode node) throws SerializationException {
            final ConfigurationNode hostileNode = node.node(HOSTILE);
            final ConfigurationNode neutralNode = node.node(NEUTRAL);
            final ConfigurationNode passiveNode = node.node(PASSIVE);
            final ConfigurationNode bossNode = node.node(BOSS);

            final String id = node.key().toString();
            final int hostile = hostileNode.getInt(0);
            final int neutral = neutralNode.getInt(0);
            final int passive = passiveNode.getInt(0);
            final int boss = bossNode.getInt(0);

            validateChance(hostileNode,hostile, HOSTILE);
            validateChance(neutralNode, neutral, NEUTRAL);
            validateChance(passiveNode, passive, PASSIVE);
            validateChance(bossNode, boss, BOSS);

            return new Chance(id, hostile, neutral, passive, boss);
        }

        //Only implemented this since it's required. We don't actually use this feature yet.
        //We need to figure out how to target a specific node from chance.getId();
        @Override
        public void serialize(Type type, @Nullable Chance chance, ConfigurationNode target) throws SerializationException {
            if(chance == null) {
                target.raw(null);
                return;
            }

            target.node(HOSTILE).set(chance.getHostile());
            target.node(NEUTRAL).set(chance.getNeutral());
            target.node(PASSIVE).set(chance.getPassive());
            target.node(BOSS).set(chance.getBoss());
        }

        private void validateChance(final ConfigurationNode node, final int chance, final String name) throws SerializationException{
            if (chance > MAX_CHANCE || chance < 0) {
                throw new SerializationException(node, int.class, name + " chance must be between 1 and 100,000. This chance was " + chance);
            }
        }

    }
}
