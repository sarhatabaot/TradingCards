package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import com.lapzupi.dev.config.Transformation;
import com.lapzupi.dev.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.drop.DropPoolEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.drop.DropPoolEntryType;
import net.tinetwork.tradingcards.tradingcardsplugin.drop.MobDropPool;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DropPoolsConfig extends YamlConfigurateFile<TradingCards> {
    private static final Set<String> RESERVED_DROP_KEYS = Set.of("enabled", "dropmin", "dropmax", "dropchance");

    private Map<EntityType, MobDropPool> mobDropPools = Collections.emptyMap();

    public DropPoolsConfig(final TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings" + File.separator, "drop-pools.yml", "settings");
    }

    @Override
    protected void initValues() {
        final Map<EntityType, MobDropPool> newPools = new EnumMap<>(EntityType.class);

        for (Map.Entry<Object, ? extends ConfigurationNode> nodeEntry : rootNode.childrenMap().entrySet()) {
            final Object keyObject = nodeEntry.getKey();
            if (keyObject == null) {
                continue;
            }

            final String key = keyObject.toString();
            if ("config-version".equalsIgnoreCase(key)) {
                continue;
            }

            final EntityType entityType = parseEntityType(key);
            if (entityType == null || !entityType.isAlive()) {
                plugin.getLogger().warning("Ignoring invalid drop-pool entity key: " + key);
                continue;
            }

            final MobDropPool mobDropPool = parsePool(entityType, key, nodeEntry.getValue());
            newPools.put(entityType, mobDropPool);
        }

        this.mobDropPools = Collections.unmodifiableMap(newPools);
    }

    private @NotNull MobDropPool parsePool(final @NotNull EntityType entityType, final @NotNull String configKey, final @NotNull ConfigurationNode poolNode) {
        final @Nullable String nameCheck = poolNode.node("namecheck").getString(null);
        final ConfigurationNode dropsNode = poolNode.node("drops");

        final boolean enabled = dropsNode.node("enabled").getBoolean(false);
        final int dropMin = Math.max(0, dropsNode.node("dropmin").getInt(1));
        final int dropMax = Math.max(dropMin, dropsNode.node("dropmax").getInt(dropMin));
        final int dropChance = parsePoolDropChance(configKey, dropsNode);
        final List<DropPoolEntry> entries = parseEntries(configKey, dropsNode);

        if (enabled && entries.isEmpty()) {
            plugin.getLogger().warning("Drop pool '" + configKey + "' is enabled but has no valid entries.");
        }

        return new MobDropPool(entityType, nameCheck, enabled, dropMin, dropMax, dropChance, entries);
    }

    private int parsePoolDropChance(final @NotNull String configKey, final @NotNull ConfigurationNode dropsNode) {
        final int dropChance = dropsNode.node("dropchance").getInt(-1);
        if (dropChance < -1 || dropChance > CardUtil.RANDOM_MAX) {
            plugin.getLogger().warning("Drop pool '" + configKey + "' has invalid drops.dropchance value '" + dropChance + "'. Using default mob chance.");
            return -1;
        }
        return dropChance;
    }

    private @NotNull List<DropPoolEntry> parseEntries(final @NotNull String configKey, final @NotNull ConfigurationNode dropsNode) {
        final List<DropPoolEntry> entries = new ArrayList<>();

        for (Map.Entry<Object, ? extends ConfigurationNode> entryNode : dropsNode.childrenMap().entrySet()) {
            final Object keyObj = entryNode.getKey();
            if (keyObj == null) {
                continue;
            }

            final String rawKey = keyObj.toString();
            if (RESERVED_DROP_KEYS.contains(rawKey.toLowerCase(Locale.ROOT))) {
                continue;
            }

            final DropPoolEntryType.ParsedDropPoolKey parsedKey = DropPoolEntryType.parseConfigKey(rawKey);
            if (parsedKey == null) {
                plugin.getLogger().warning("Drop pool '" + configKey + "' has invalid entry key '" + rawKey + "'. Expected card_<id> or rarity_<id>.");
                continue;
            }

            final ConfigurationNode dropEntryNode = entryNode.getValue();
            final int dropWeight = dropEntryNode.node("dropchance").getInt(dropEntryNode.getInt(0));
            if (dropWeight <= 0) {
                plugin.getLogger().warning("Drop pool '" + configKey + "' entry '" + rawKey + "' has non-positive dropchance and will be ignored.");
                continue;
            }

            entries.add(new DropPoolEntry(parsedKey.type(), parsedKey.id(), dropWeight));
        }

        return entries;
    }

    private @Nullable EntityType parseEntityType(final @NotNull String entityId) {
        try {
            return EntityType.valueOf(entityId.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public @Nullable MobDropPool getMobDropPool(@NotNull LivingEntity entity) {
        final MobDropPool mobDropPool = this.mobDropPools.get(entity.getType());
        if (mobDropPool == null) {
            return null;
        }

        if (mobDropPool.matches(entity)) {
            return mobDropPool;
        }

        return null;
    }

    @Override
    protected void builderOptions(TypeSerializerCollection.Builder builder) {
        // Nothing extra needed.
    }

    @Override
    protected Transformation getTransformation() {
        return null;
    }
}
