package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigurate;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PacksConfig extends SimpleConfigurate {
    private List<String> packs;
    public PacksConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings"+ File.separator,"packs.yml", "settings");

    }

    public Pack getPack(final String name) throws SerializationException {
        return rootNode.node(name).get(Pack.class);
    }

    public List<String> getPacks() {
        return packs;
    }

    @Override
    protected void preLoaderBuild() {
        PackSerializer.init(plugin);
        loaderBuilder.defaultOptions(opts -> opts.serializers(builder ->
                builder.registerExact(Pack.class, PackSerializer.INSTANCE)));
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.packs = new ArrayList<>();
        for(Map.Entry<Object, ? extends ConfigurationNode> nodeEntry: rootNode.childrenMap().entrySet()) {
            final String name = nodeEntry.getValue().key().toString();
            packs.add(name);
        }
    }

    public static class PackSerializer implements TypeSerializer<Pack> {
        private static TradingCards plugin;
        public static final PackSerializer INSTANCE = new PackSerializer();
        private static final String CONTENT = "content";
        private static final String SERIES = "series";
        private static final String PRICE = "prices";
        private static final String PERMISSION = "permission";
        private static final String DISPLAY_NAME = "display-name";

        public static void init(TradingCards plugin) {
            PackSerializer.plugin =plugin;
        }
        private PackSerializer() {
        }

        @Override
        public Pack deserialize(Type type, ConfigurationNode node) throws SerializationException {
            final ConfigurationNode contentNode = node.node(CONTENT);
            final ConfigurationNode seriesNode = node.node(SERIES);
            final ConfigurationNode priceNode = node.node(PRICE);
            final ConfigurationNode permissionsNode = node.node(PERMISSION);
            final ConfigurationNode displayNameNode = node.node(DISPLAY_NAME);

            final List<String> contentStringList = contentNode.getList(String.class);
            final List<Pack.PackEntry> packEntryList = new ArrayList<>();
            for(String entry: contentStringList) {
                packEntryList.add(Pack.PackEntry.fromString(entry));
            }
            final String series = seriesNode.getString();
            final double price = priceNode.getDouble(0.0D);
            final String permissions = permissionsNode.getString();
            final String displayName = displayNameNode.getString();

            return new Pack(packEntryList,displayName, series,price,permissions);
        }

        //Only implemented this since it's required. We don't actually use this feature yet.
        @Override
        public void serialize(Type type, @Nullable Pack pack, ConfigurationNode target) throws SerializationException {
            if(pack == null) {
                target.raw(null);
                return;
            }

            target.node(CONTENT).set(pack.getPackEntryList());
            target.node(SERIES).set(pack.getSeries());
            target.node(PRICE).set(pack.getPrice());
            target.node(PERMISSION).set(pack.getPermissions());
        }
    }
}
