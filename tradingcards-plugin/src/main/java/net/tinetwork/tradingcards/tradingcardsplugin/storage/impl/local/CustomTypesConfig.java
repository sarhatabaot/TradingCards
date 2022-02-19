package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local;

import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.api.config.SimpleConfigurate;
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
public class CustomTypesConfig extends SimpleConfigurate {
    private Set<DropType> dropTypes;

    public CustomTypesConfig(final TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings"+ File.separator, "custom-types.yml", "settings");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.dropTypes = new HashSet<>();
        for(Map.Entry<Object, ? extends ConfigurationNode> nodeEntry: rootNode.childrenMap().entrySet()) {
            final String dropTypeKey = nodeEntry.getValue().key().toString();
            try {
                dropTypes.add(getDropType(dropTypeKey));
            } catch (SerializationException e){
                Util.logSevereException(e);
                plugin.debug(CustomTypesConfig.class,"Couldn't add="+dropTypeKey);
            }
        }
        plugin.debug(CustomTypesConfig.class,"Total Custom Types="+dropTypes.size());
    }

    public Set<DropType> getDropTypes() {
        return dropTypes;
    }

    @Override
    protected void preLoaderBuild() {
        loaderBuilder.defaultOptions(opts -> opts.serializers(builder ->
                builder.registerExact(DropTypeSerializer.TYPE, DropTypeSerializer.INSTANCE)));
    }

    public DropType getDropType(final String name) throws SerializationException {
        return rootNode.node(name).get(DropType.class);
    }

    public void createCustomType(final String typeId, final String type) {
        try {
            rootNode.node(typeId).set(new DropType(typeId,typeId,type));
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public static class DropTypeSerializer implements TypeSerializer<DropType> {
        public static final DropTypeSerializer INSTANCE = new DropTypeSerializer();
        public static final Class<DropType> TYPE = DropType.class;

        private static final String DISPLAY_NAME = "display-name";
        private static final String DROP_TYPE = "drop-type";
        private DropTypeSerializer(){

        }
        @Override
        public DropType deserialize(final Type type, final @NotNull ConfigurationNode node) throws SerializationException {
            final String id = node.key().toString();
            final String displayName = node.node(DISPLAY_NAME).getString();
            final String dropType = node.node(DROP_TYPE).getString();
            return new DropType(id,displayName,dropType);
        }

        @Override
        public void serialize(final Type type, @Nullable final DropType obj, final ConfigurationNode target) throws SerializationException {
            if(obj == null) {
                target.set(null);
                return;
            }

            target.node(DISPLAY_NAME).set(obj.getDisplayName());
            target.node(DROP_TYPE).set(obj.getType());
        }
    }
}
