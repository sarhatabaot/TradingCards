package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local;

import com.github.sarhatabaot.kraken.core.config.HoconConfigurateFile;
import com.github.sarhatabaot.kraken.core.config.Transformation;
import net.tinetwork.tradingcards.api.model.MobGroup;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.bukkit.entity.EntityType;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * @author sarhatabaot
 */
public class MobGroupsConfig extends HoconConfigurateFile<TradingCards> {
    private Map<String, MobGroup> mobGroups;

    public MobGroupsConfig(@NotNull final TradingCards plugin) throws ConfigurateException {
        super(plugin, "data" + File.separator, "mob-groups.conf", "data");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.mobGroups = new HashMap<>();

        for(Map.Entry<Object, CommentedConfigurationNode> entry: rootNode.childrenMap().entrySet()) {
            mobGroups.put(entry.getKey().toString(), entry.getValue().get(MobGroup.class));
        }
    }

    @Override
    protected void builderOptions() {
        loaderBuilder.defaultOptions(opts -> opts.serializers(builder ->
                builder.registerExact(MobGroupSerializer.TYPE, MobGroupSerializer.INSTANCE)));
    }

    @Override
    protected Transformation getTransformation() {
        return null;
    }

    public static class MobGroupSerializer implements TypeSerializer<MobGroup> {
        public static final MobGroupSerializer INSTANCE = new MobGroupSerializer();
        public static final Class<MobGroup> TYPE = MobGroup.class;
        private static final String DISPLAY_NAME = "display-name";
        private static final String ENTITY_TYPES = "entity-types";
        private static final String GROUPS = "groups";

        @Override
        public MobGroup deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
            final String id = node.key().toString();
            final String displayName = node.node(DISPLAY_NAME).getString(id);
            final List<EntityType> entityTypesList = node.node(ENTITY_TYPES).getList(EntityType.class, Collections.emptyList()); //make set by filtering out duplicates
            final List<MobGroup> mobGroupsList = node.node(GROUPS).getList(MobGroup.class, Collections.emptyList());

            if(mobGroupsList.isEmpty()) {
                return new MobGroup(id, displayName, new HashSet<>(entityTypesList));
            }

            return new MobGroup(id,displayName,new HashSet<>(entityTypesList),new HashSet<>(mobGroupsList));
        }

        @Override
        public void serialize(final Type type, @Nullable final MobGroup group, final ConfigurationNode node) throws SerializationException {
            if(group == null) {
                node.raw(null);
                return;
            }

            node.node(DISPLAY_NAME).set(group.getDisplayName());
            node.node(ENTITY_TYPES).setList(EntityType.class, new ArrayList<>(group.getEntityTypes()));

            if(!group.getGroups().isEmpty())
                node.node(GROUPS).setList(String.class,group.getGroups().stream().map(MobGroup::getId).toList());
        }
    }

    public Map<String, MobGroup> getMobGroups() {
        return mobGroups;
    }

    public void addGroup(final String mobGroupId, final MobGroup group) {
        final CommentedConfigurationNode groupNode = rootNode.node(mobGroupId);
        try {
            MobGroup currentGroup = groupNode.get(MobGroup.class);
            currentGroup.addGroup(group);
            groupNode.set(currentGroup);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }
    public void removeGroup(final String mobGroupId, final MobGroup group) {
        final CommentedConfigurationNode groupNode = rootNode.node(mobGroupId);
        try {
            MobGroup currentGroup = groupNode.get(MobGroup.class);
            currentGroup.removeGroup(group);
            groupNode.set(currentGroup);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public void addEntityType(final String mobGroupId, final EntityType entityType) {
        final CommentedConfigurationNode groupNode = rootNode.node(mobGroupId);
        try {
            MobGroup currentGroup = groupNode.get(MobGroup.class);
            currentGroup.addEntityType(entityType);
            groupNode.set(currentGroup);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public void removeEntityType(final String mobGroupId, final EntityType entityType) {
        final CommentedConfigurationNode groupNode = rootNode.node(mobGroupId);
        try {
            MobGroup currentGroup = groupNode.get(MobGroup.class);
            currentGroup.removeEntityType(entityType);
            groupNode.set(currentGroup);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public void setDisplayName(final String mobGroupId, final String displayName) {
        final CommentedConfigurationNode groupNode = rootNode.node(mobGroupId);
        try {
            MobGroup currentGroup = groupNode.get(MobGroup.class);
            currentGroup.setDisplayName(displayName);
            groupNode.set(currentGroup);
            loader.save(rootNode);
            reloadConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

}
