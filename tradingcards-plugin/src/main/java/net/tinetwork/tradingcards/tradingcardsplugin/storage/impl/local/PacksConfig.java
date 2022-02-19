package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local;

import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.api.config.SimpleConfigurate;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
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
    private List<String> packNames;
    private List<Pack> packs;
    public PacksConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings"+ File.separator,"packs.yml", "settings");
    }

    public Pack getPack(final String name) throws SerializationException {
        return rootNode.node(name).get(Pack.class);
    }

    public List<String> getPackNames() {
        return packNames;
    }

    public List<Pack> getPacks() {
        return packs;
    }


    public void createPack(final String packId){
        try {
            ConfigurationNode packNode = rootNode.node(packId).set(new Pack(packId,new ArrayList<>(),packId,100.0,"cards.packs."+packId));
            loader.save(packNode);
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    @Override
    protected void preLoaderBuild() {
        loaderBuilder.defaultOptions(opts -> opts.serializers(builder ->
                builder.registerExact(Pack.class, PackSerializer.INSTANCE)));
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.packNames = new ArrayList<>();
        this.packs = new ArrayList<>();
        for(Map.Entry<Object, ? extends ConfigurationNode> nodeEntry: rootNode.childrenMap().entrySet()) {
            final String name = nodeEntry.getValue().key().toString();
            final Pack pack = getPack(name);
            packs.add(pack);
            packNames.add(name);
        }
    }

    public static class PackSerializer implements TypeSerializer<Pack> {
        public static final PackSerializer INSTANCE = new PackSerializer();
        private static final String CONTENT = "content";
        private static final String PRICE = "prices";
        private static final String PERMISSION = "permission";
        private static final String DISPLAY_NAME = "display-name";

        private PackSerializer() {
        }

        @Override
        public Pack deserialize(Type type, ConfigurationNode node) throws SerializationException {
            final ConfigurationNode contentNode = node.node(CONTENT);
            final ConfigurationNode priceNode = node.node(PRICE);
            final ConfigurationNode permissionsNode = node.node(PERMISSION);
            final ConfigurationNode displayNameNode = node.node(DISPLAY_NAME);
            final String id = node.key().toString();

            final List<String> contentStringList = contentNode.getList(String.class);
            final List<Pack.PackEntry> packEntryList = new ArrayList<>();
            for(String entry: contentStringList) {
                packEntryList.add(Pack.PackEntry.fromString(entry));
            }
            final double price = priceNode.getDouble(0.0D);
            final String permissions = permissionsNode.getString();
            final String displayName = getDisplayName(displayNameNode.getString(),node);

            return new Pack(id,packEntryList,displayName, price,permissions);
        }

        private String getDisplayName(final String displayName, final ConfigurationNode node) {
            if(displayName == null) {
                final String nodeKey = node.key().toString();
                return nodeKey.replace("_"," ");
            }

            return displayName;
        }

        //Only implemented this since it's required. We don't actually use this feature yet.
        @Override
        public void serialize(Type type, @Nullable Pack pack, ConfigurationNode target) throws SerializationException {
            if(pack == null) {
                target.raw(null);
                return;
            }

            target.node(CONTENT).set(pack.getPackEntryList());
            target.node(PRICE).set(pack.getPrice());
            target.node(PERMISSION).set(pack.getPermissions());
        }
    }
}
