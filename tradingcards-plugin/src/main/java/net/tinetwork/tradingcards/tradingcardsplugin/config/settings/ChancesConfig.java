package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import com.github.sarhatabaot.kraken.core.config.ConfigurateFile;
import net.tinetwork.tradingcards.api.model.chance.Chance;
import net.tinetwork.tradingcards.api.model.chance.EmptyChance;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalExceptions;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.settings.Chances;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.io.File;
import java.lang.reflect.Type;

public class ChancesConfig extends ConfigurateFile<TradingCards> {
    private int hostileChance;
    private int neutralChance;
    private int passiveChance;
    private int bossChance;
    private boolean bossDrop;
    private int bossDropRarity;
    private int shinyVersionChance;
    private int allChance;

    public ChancesConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings" + File.separator, "chances.yml", "settings");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.hostileChance = rootNode.node("hostile-chance").getInt(Chances.HOSTILE_CHANCE);
        this.neutralChance = rootNode.node("neutral-chance").getInt(Chances.NEUTRAL_CHANCE);
        this.passiveChance = rootNode.node("passive-chance").getInt(Chances.PASSIVE_CHANCE);
        this.bossChance = rootNode.node("boss-chance").getInt(Chances.BOSS_CHANCE);
        this.bossDrop = rootNode.node("boss-drop").getBoolean(Chances.BOSS_DROP);
        this.bossDropRarity = rootNode.node("boss-drop-rarity").getInt(Chances.BOSS_DROP_RARITY);
        this.shinyVersionChance = rootNode.node("shiny-version-chance").getInt(Chances.SHINY_VERSION_CHANCE);
        this.allChance = rootNode.node("all-chance").getInt(Chances.ALL_CHANCE);
    }

    @Override
    protected void preLoaderBuild() {
        loaderBuilder.defaultOptions(opts -> opts.serializers(builder ->
                builder.registerExact(Chance.class, ChanceSerializer.INSTANCE)));
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

    public int allChance() {
        return allChance;
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
                throw new SerializationException(node, int.class, InternalExceptions.INVALID_CHANCE.formatted(name,chance));
            }
        }

    }
}
