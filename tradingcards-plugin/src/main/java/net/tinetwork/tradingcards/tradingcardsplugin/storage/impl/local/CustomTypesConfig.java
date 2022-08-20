package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local;

import com.github.sarhatabaot.kraken.core.config.Transformation;
import com.github.sarhatabaot.kraken.core.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.MobGroup;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author sarhatabaot
 */
public class CustomTypesConfig extends YamlConfigurateFile<TradingCards> {
    private Set<DropType> dropTypes;

    public CustomTypesConfig(final TradingCards plugin) throws ConfigurateException {
        super(plugin, "data"+ File.separator, "custom-types.yml", "data");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.dropTypes = new HashSet<>();
        for(Map.Entry<Object, ? extends ConfigurationNode> nodeEntry: rootNode.childrenMap().entrySet()) {
            final String dropTypeKey = nodeEntry.getValue().key().toString();
            try {
                dropTypes.add(getCustomType(dropTypeKey));
            } catch (SerializationException e){
                Util.logSevereException(e);
                plugin.debug(CustomTypesConfig.class,"Couldn't add="+dropTypeKey);
            }
        }
        plugin.debug(CustomTypesConfig.class,"Total Custom Types="+dropTypes.size());
    }
    public void editType(final String typeId, final MobGroup dropType) {
        final ConfigurationNode dropTypeNode = rootNode.node(typeId);
        try {
            DropType selectedType = getCustomType(typeId);
            selectedType.setMobGroup(dropType);
            dropTypeNode.set(selectedType);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }

    }
    public void editDisplayName(final String typeId, final String displayName) {
        final ConfigurationNode dropTypeNode = rootNode.node(typeId);
        try {
            DropType selectedType = getCustomType(typeId);
            selectedType.setDisplayName(displayName);
            dropTypeNode.set(selectedType);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public Set<DropType> getCustomTypes() {
        return dropTypes;
    }



    @Override
    protected void builderOptions() {
        loaderBuilder.defaultOptions(opts -> opts.serializers(builder ->
                builder.registerExact(DropType.class, new DropTypeSerializer())));
    }

    public DropType getCustomType(final String typeId) throws SerializationException {
        return rootNode.node(typeId).get(DropType.class);
    }

    public void createCustomType(final String typeId, final MobGroup type) {
        try {
            rootNode.node(typeId).set(new DropType(typeId,typeId,type));
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public class DropTypeSerializer implements TypeSerializer<DropType> {
        private static final String DISPLAY_NAME = "display-name";
        private static final String DROP_TYPE = "drop-type";
        private DropTypeSerializer(){

        }
        @Override
        public DropType deserialize(final Type type, final @NotNull ConfigurationNode node) throws SerializationException {
            final String id = node.key().toString();
            final String displayName = node.node(DISPLAY_NAME).getString();
            final String groupId = node.node(DROP_TYPE).getString();
            final MobGroup dropType = plugin.getMobGroupManager().getMobGroup(groupId).get();
            return new DropType(id,displayName,dropType);
        }

        @Override
        public void serialize(final Type type, @Nullable final DropType obj, final ConfigurationNode target) throws SerializationException {
            if(obj == null) {
                target.set(null);
                return;
            }

            target.node(DISPLAY_NAME).set(obj.getDisplayName());
            target.node(DROP_TYPE).set(obj.getMobGroup());
        }
    }

    @Override
    protected Transformation getTransformation() {
        return null;
    }
}
