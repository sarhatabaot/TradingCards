package net.tinetwork.tradingcards.tradingcardsplugin.managers;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.tinetwork.tradingcards.api.manager.TypeManager;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalLog;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DropTypeManager extends Manager<String, DropType> implements TypeManager {
    private static final String HOSTILE_ID  = "hostile";
    private static final String NEUTRAL_ID = "neutral";
    private static final String PASSIVE_ID = "passive";
    private static final String BOSS_ID = "bossId";
    private static final String ALL_ID = "allId";

    public static final Map<String,DropType> DEFAULT_MOB_TYPES = Map.of(
            HOSTILE_ID, new DropType(HOSTILE_ID,"Hostile", HOSTILE_ID),
            NEUTRAL_ID, new DropType(NEUTRAL_ID, "Neutral", NEUTRAL_ID),
            PASSIVE_ID, new DropType(PASSIVE_ID, "Passive", PASSIVE_ID),
            BOSS_ID, new DropType(BOSS_ID, "Boss", BOSS_ID),
            ALL_ID, new DropType(ALL_ID,"All", ALL_ID)
    );

    public static final DropType HOSTILE = DEFAULT_MOB_TYPES.get(HOSTILE_ID);
    public static final DropType NEUTRAL = DEFAULT_MOB_TYPES.get(NEUTRAL_ID);
    public static final DropType PASSIVE = DEFAULT_MOB_TYPES.get(PASSIVE_ID);
    public static final DropType BOSS = DEFAULT_MOB_TYPES.get(BOSS_ID);
    public static final DropType ALL = DEFAULT_MOB_TYPES.get(ALL_ID);


    public DropTypeManager(final TradingCards plugin) {
        super(plugin);
        this.plugin.getLogger().info(() -> InternalLog.Init.LOAD_DROPTYPE_MANAGER);
    }

    @Override
    public DropType getType(final @NotNull String type) {
        return cache.getUnchecked(type.toLowerCase());
    }

    @Override
    public LoadingCache<String, DropType> loadCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(plugin.getAdvancedConfig().getTypes().maxCacheSize())
                .refreshAfterWrite(plugin.getAdvancedConfig().getTypes().refreshAfterWrite(), TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public DropType load(final String key) throws Exception {
                        return plugin.getStorage().getCustomType(key);
                    }
                });
    }

    @Override
    public void preLoadCache() {
        super.preLoadCache();
        cache.putAll(DEFAULT_MOB_TYPES);
    }

    @Override
    public void forceCacheRefresh() {
        super.forceCacheRefresh();
        cache.putAll(DEFAULT_MOB_TYPES);
    }

    @Override
    public List<String> getKeys() {
        return plugin.getStorage().getDropTypes().stream().map(DropType::getId).toList();
    }

    @Override
    public Map<String, DropType> getTypes() {
        return cache.asMap();
    }

    @Override
    public DropType getMobType(final EntityType type) {
        return CardUtil.getMobType(type);
    }

    @Override
    public boolean containsType(final String typeId) {
        return cache.getIfPresent(typeId) != null;
    }

    @Override
    public List<DropType> getDefaultTypes() {
        return DEFAULT_MOB_TYPES.values().stream().toList();
    }

    public Set<String> getAllTypesIds() {
        return new HashSet<>(getKeys());
    }
}
