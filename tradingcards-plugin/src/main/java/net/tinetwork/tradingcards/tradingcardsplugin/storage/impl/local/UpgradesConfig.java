package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local;


import com.lapzupi.dev.config.Transformation;
import com.lapzupi.dev.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.api.model.Upgrade;
import net.tinetwork.tradingcards.api.model.pack.PackEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.LoggerUtil;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
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

/**
 * @author sarhatabaot
 */
public class UpgradesConfig extends YamlConfigurateFile<TradingCards> {
    private List<Upgrade> upgrades;
    public UpgradesConfig(@NotNull final TradingCards plugin) throws ConfigurateException {
        super(plugin, "data"+ File.separator,"upgrades.yml", "data");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.upgrades = new ArrayList<>();
        for(Map.Entry<Object, ? extends ConfigurationNode> nodeEntry: rootNode.childrenMap().entrySet()) {
            final String upgradeId = nodeEntry.getValue().key().toString();
            final Upgrade upgrade = getUpgrade(upgradeId);
            upgrades.add(upgrade);
        }

    }
    
    @Override
    protected void builderOptions(TypeSerializerCollection.Builder builder) {
        builder.registerExact(Upgrade.class, UpgradeSerializer.INSTANCE);
    }
    

    @Override
    protected Transformation getTransformation() {
        return null;
    }

    public List<Upgrade> getUpgrades() {
        return upgrades;
    }

    public void createUpgrade(final String upgradeId, final PackEntry required, final PackEntry result) {
        try {
            rootNode.node(upgradeId).set(new Upgrade(upgradeId,required,result));
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e){
            LoggerUtil.logSevereException(e);
        }
    }

    public Upgrade getUpgrade(final String upgradeId) throws SerializationException {
        return rootNode.node(upgradeId).get(Upgrade.class);
    }

    public void editUpgradeRequired(final String upgradeId, final PackEntry required) {
        ConfigurationNode upgradeNode = rootNode.node(upgradeId);
        try {
            Upgrade upgrade = getUpgrade(upgradeId);
            upgrade.setRequired(required);
            upgradeNode.set(upgrade);
            loader.save(rootNode);
        } catch (ConfigurateException e) {
            LoggerUtil.logSevereException(e);
        }
    }

    public void editUpgradeResult(final String upgradeId, final PackEntry result) {
        ConfigurationNode upgradeNode = rootNode.node(upgradeId);
        try {
            Upgrade upgrade = getUpgrade(upgradeId);
            upgrade.setResult(result);
            upgradeNode.set(upgrade);
            loader.save(rootNode);
        } catch (ConfigurateException e) {
            LoggerUtil.logSevereException(e);
        }
    }

    public void deleteUpgrade(final String upgradeId) {
        ConfigurationNode upgradeNode = rootNode.node(upgradeId);
        try {
            upgradeNode.set(null);
            loader.save(rootNode);
        } catch (ConfigurateException e) {
            LoggerUtil.logSevereException(e);
        }
    }

    public static class UpgradeSerializer implements TypeSerializer<Upgrade> {
        public static final UpgradeSerializer INSTANCE = new UpgradeSerializer();

        private static final String REQUIRED = "required";
        private static final String RESULT = "result";

        private UpgradeSerializer() {

        }
        @Override
        public Upgrade deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
            final String id = node.key().toString();

            final PackEntry required = PackEntry.fromString(node.node(REQUIRED).getString());
            final PackEntry result = PackEntry.fromString(node.node(RESULT).getString());
            return new Upgrade(id,required,result);
        }

        @Override
        public void serialize(final Type type, @Nullable final Upgrade upgrade, final ConfigurationNode target) throws SerializationException {
            if(upgrade == null) {
                target.raw(null);
                return;
            }

            target.node(REQUIRED).set(upgrade.required().toString());
            target.node(RESULT).set(upgrade.result().toString());
        }
    }
}
