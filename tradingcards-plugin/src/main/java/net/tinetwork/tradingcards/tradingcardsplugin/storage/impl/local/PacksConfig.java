package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local;


import com.lapzupi.dev.config.Transformation;
import com.lapzupi.dev.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.api.model.pack.Pack;
import net.tinetwork.tradingcards.api.model.pack.PackEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PacksConfig extends YamlConfigurateFile<TradingCards> {
    private List<String> packNames;
    private List<Pack> packs;
    public PacksConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "data"+ File.separator,"packs.yml", "data");
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

    public void editDisplayName(final String packId, final String displayName) {
        ConfigurationNode packNode = rootNode.node(packId);
        try {
            Pack pack = getPack(packId);
            pack.setDisplayName(displayName);
            packNode.set(pack);
            loader.save(rootNode);
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }

    }

    public void editContents(final String packId, final int lineNumber, final PackEntry packEntry) {
        ConfigurationNode packNode = rootNode.node(packId);
        try {
            Pack pack = getPack(packId);
            pack.getPackEntryList().set(lineNumber,packEntry);
            packNode.set(pack);
            loader.save(rootNode);
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public void editTradeCards(final String packId, final int lineNumber, final PackEntry packEntry) {
        ConfigurationNode packNode = rootNode.node(packId);
        try {
            Pack pack = getPack(packId);
            pack.getTradeCards().set(lineNumber,packEntry);
            packNode.set(pack);
            loader.save(rootNode);
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public void editPermission(final String packId, final String permission) {
        ConfigurationNode packNode = rootNode.node(packId);
        try {
            Pack pack = getPack(packId);
            pack.setPermission(permission);
            packNode.set(pack);
            loader.save(rootNode);
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public void editPrice(final String packId, final double price){
        ConfigurationNode packNode = rootNode.node(packId);
        try {
            Pack pack = getPack(packId);
            pack.setBuyPrice(price);
            packNode.set(pack);
            loader.save(rootNode);
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public void editCurrencyId(final String packId, final String currencyId) {
        ConfigurationNode packNode = rootNode.node(packId);
        try {
            Pack pack = getPack(packId);
            pack.setCurrencyId(currencyId);
            packNode.set(pack);
            loader.save(rootNode);
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }


    public void createPack(final String packId){
        try {
            rootNode.node(packId).set(new Pack(packId,new ArrayList<>(),packId,100.0,"","cards.packs."+packId, new ArrayList<>()));
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }
    
    @Override
    protected void builderOptions(TypeSerializerCollection.Builder builder) {
        builder.registerExact(Pack.class, PackSerializer.INSTANCE);
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
        private static final String PRICE = "price";
        private static final String PERMISSION = "permission";
        private static final String DISPLAY_NAME = "display-name";
        private static final String CURRENCY_ID = "currency-id";
        private static final String TRADE = "trade";

        private PackSerializer() {
        }

        @Override
        public Pack deserialize(Type type, ConfigurationNode node) throws SerializationException {
            final ConfigurationNode contentNode = node.node(CONTENT);
            final ConfigurationNode tradeNode = node.node(TRADE);
            final ConfigurationNode priceNode = node.node(PRICE);
            final ConfigurationNode permissionsNode = node.node(PERMISSION);
            final ConfigurationNode displayNameNode = node.node(DISPLAY_NAME);
            final String currencyId = node.node(CURRENCY_ID).getString("tc-internal-default");
            final String id = node.key().toString();

            final List<String> contentStringList = contentNode.getList(String.class, new ArrayList<>());
            final List<PackEntry> packEntryList = new ArrayList<>();
            for(String entry: contentStringList) {
                packEntryList.add(PackEntry.fromString(entry));
            }
            final double price = priceNode.getDouble(0.0D);
            final String permissions = permissionsNode.getString();
            final String displayName = getDisplayName(displayNameNode.getString(),node);

            final List<String> tradeStringList = tradeNode.getList(String.class, new ArrayList<>());
            final List<PackEntry> tradeCardList = new ArrayList<>();
            for(String entry: tradeStringList) {
                tradeCardList.add(PackEntry.fromString(entry));
            }

            return new Pack(id,packEntryList,displayName, price,currencyId,permissions,tradeCardList);
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
            target.node(PRICE).set(pack.getBuyPrice());
            target.node(PERMISSION).set(pack.getPermission());
        }
    }

    @Override
    protected Transformation getTransformation() {
        return null;
    }
}
