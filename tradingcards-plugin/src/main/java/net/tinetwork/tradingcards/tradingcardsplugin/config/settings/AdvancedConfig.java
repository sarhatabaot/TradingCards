package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import com.lapzupi.dev.config.Transformation;
import com.lapzupi.dev.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.config.transformations.AdvancedTransformations;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.settings.Advanced;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.File;
import java.lang.reflect.Type;

/**
 * @author sarhatabaot
 */
public class AdvancedConfig extends YamlConfigurateFile<TradingCards> {
    private ConfigCache rarity;
    private ConfigCache series;
    private ConfigCache cards;
    private ConfigCache types;
    private ConfigCache packs;

    private ConfigCache upgrades;


    public AdvancedConfig(@NotNull final TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings" + File.separator, "advanced.yml", "settings");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.rarity = rootNode.node("rarity").get(ConfigCache.class, new ConfigCache(Advanced.Cache.Rarity.MAX_CACHE_ENTRIES,Advanced.Cache.Rarity.REFRESH_AFTER_WRITE));
        this.series = rootNode.node("series").get(ConfigCache.class, new ConfigCache(Advanced.Cache.Series.MAX_CACHE_ENTRIES,Advanced.Cache.Series.REFRESH_AFTER_WRITE));
        this.cards = rootNode.node("cards").get(ConfigCache.class, new ConfigCache(Advanced.Cache.Cards.MAX_CACHE_ENTRIES,Advanced.Cache.Cards.REFRESH_AFTER_WRITE));
        this.types = rootNode.node("types").get(ConfigCache.class, new ConfigCache(Advanced.Cache.Types.MAX_CACHE_ENTRIES,Advanced.Cache.Types.REFRESH_AFTER_WRITE));
        this.packs = rootNode.node("packs").get(ConfigCache.class, new ConfigCache(Advanced.Cache.Packs.MAX_CACHE_ENTRIES,Advanced.Cache.Packs.REFRESH_AFTER_WRITE));
        this.upgrades = rootNode.node("upgrades").get(ConfigCache.class, new ConfigCache(Advanced.Cache.Upgrades.MAX_CACHE_ENTRIES,Advanced.Cache.Upgrades.REFRESH_AFTER_WRITE));
    }
    
    @Override
    protected void builderOptions(TypeSerializerCollection.Builder builder) {
        builder.registerExact(ConfigCache.class,  ConfigCacheSerializer.INSTANCE);
    }
    

    public record ConfigCache(int maxCacheSize, int refreshAfterWrite) {
    }

    public static class ConfigCacheSerializer implements TypeSerializer<ConfigCache> {
        public static final ConfigCacheSerializer INSTANCE = new ConfigCacheSerializer();

        private static final String MAX_CACHE_ENTRIES = "max-cache-entries";
        private static final String REFRESH_AFTER_WRITE = "refresh-after-write";
        @Override
        public ConfigCache deserialize(final Type type, final @NotNull ConfigurationNode node) throws SerializationException {
            final int maxCacheEntries = node.node(MAX_CACHE_ENTRIES).getInt(1000);
            final int refreshAfterWrite = node.node(REFRESH_AFTER_WRITE).getInt(1);
            return new ConfigCache(maxCacheEntries,refreshAfterWrite);
        }

        @Override
        public void serialize(final Type type, @Nullable final ConfigCache obj, final ConfigurationNode node) throws SerializationException {
            //This is a read only config. It must be changed through the setting files.
        }
    }

    public ConfigCache getRarity() {
        return rarity;
    }

    public ConfigCache getSeries() {
        return series;
    }

    public ConfigCache getCards() {
        return cards;
    }

    public ConfigCache getTypes() {
        return types;
    }

    public ConfigCache getPacks() {
        return packs;
    }

    public ConfigCache getUpgrades() {
        return upgrades;
    }

    @Override
    protected Transformation getTransformation() {
        return new AdvancedTransformations();
    }
}
