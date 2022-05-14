package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import com.github.sarhatabaot.kraken.core.config.ConfigurateFile;
import net.tinetwork.tradingcards.api.model.chance.Chance;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.settings.Advanced;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.io.File;
import java.lang.reflect.Type;

/**
 * @author sarhatabaot
 */
public class AdvancedConfig extends ConfigurateFile<TradingCards> {
    private ConfigCache rarity;
    private ConfigCache series;
    private ConfigCache cards;
    private ConfigCache types;
    private ConfigCache packs;


    public AdvancedConfig(@NotNull final TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings" + File.separator, "advanced.yml", "settings");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.rarity = rootNode.node("rarity").get(ConfigCache.class, new ConfigCache(Advanced.Cache.Rarity.MAX_CACHE_ENTRIES,Advanced.Cache.Rarity.REFRESH_AFTER_WRITE));
        this.rarity = rootNode.node("series").get(ConfigCache.class, new ConfigCache(Advanced.Cache.Series.MAX_CACHE_ENTRIES,Advanced.Cache.Series.REFRESH_AFTER_WRITE));
        this.rarity = rootNode.node("cards").get(ConfigCache.class, new ConfigCache(Advanced.Cache.Cards.MAX_CACHE_ENTRIES,Advanced.Cache.Cards.REFRESH_AFTER_WRITE));
        this.rarity = rootNode.node("types").get(ConfigCache.class, new ConfigCache(Advanced.Cache.Types.MAX_CACHE_ENTRIES,Advanced.Cache.Types.REFRESH_AFTER_WRITE));
        this.rarity = rootNode.node("packs").get(ConfigCache.class, new ConfigCache(Advanced.Cache.Packs.MAX_CACHE_ENTRIES,Advanced.Cache.Packs.REFRESH_AFTER_WRITE));
    }

    @Override
    protected void preLoaderBuild() {
        loaderBuilder.defaultOptions(opts -> opts.serializers(builder ->
                builder.registerExact(ConfigCache.class,  ConfigCacheSerializer.INSTANCE)));
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
            final int refreshAfterWrite = node.node(REFRESH_AFTER_WRITE).getInt(0);
            return new ConfigCache(maxCacheEntries,refreshAfterWrite);
        }

        @Override
        public void serialize(final Type type, @Nullable final ConfigCache obj, final ConfigurationNode node) throws SerializationException {
            //This is a read only config. It must be changed through the setting files.
        }
    }
}
