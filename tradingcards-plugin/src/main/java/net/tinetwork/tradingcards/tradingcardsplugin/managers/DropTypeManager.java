package net.tinetwork.tradingcards.tradingcardsplugin.managers;


import net.tinetwork.tradingcards.api.manager.TypeManager;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalLog;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DropTypeManager implements TypeManager {
    private final TradingCards plugin;
    private final List<String> allTypesIds;

    public static final DropType HOSTILE = new DropType("hostile","Hostile","hostile");
    public static final DropType NEUTRAL = new DropType("neutral", "Neutral", "neutral");
    public static final DropType PASSIVE = new DropType("passive", "Passive", "passive");
    public static final DropType BOSS = new DropType("boss", "Boss", "boss");
    public static final DropType ALL = new DropType("all","All","all");
    public static final List<DropType> DEFAULT_TYPES = List.of(HOSTILE,NEUTRAL,PASSIVE,BOSS,ALL);

    private Map<String, DropType> mobTypes;

    public DropTypeManager(final TradingCards plugin) {
        this.plugin = plugin;
        loadTypes();
        this.allTypesIds = Stream.concat(getDefaultTypes().stream().map(DropType::getId), getTypes().keySet().stream()).toList();
        this.plugin.getLogger().info(() -> InternalLog.Init.LOAD_DROPTYPE_MANAGER);
    }

    @Override
    public DropType getType(final String type) {
        if(!mobTypes.containsKey(type)) {
            return switch (type.toLowerCase()) {
                case "boss" -> BOSS;
                case "hostile" -> HOSTILE;
                case "neutral" -> NEUTRAL;
                case "passive" -> PASSIVE;
                case "all" -> ALL;
                default -> mobTypes.get(type);
            };
        }
        return mobTypes.get(type);
    }

    @Override
    public void loadTypes() {
        this.mobTypes = new HashMap<>();
        for(DropType dropType: plugin.getStorage().getDropTypes()) {
            mobTypes.put(dropType.getId(),dropType);
        }
    }

    @Override
    public Map<String, DropType> getTypes() {
        return mobTypes;
    }

    @Override
    public DropType getMobType(final EntityType type) {
        return CardUtil.getMobType(type);
    }

    @Override
    public boolean containsType(final String typeId) {
        if(DEFAULT_TYPES.stream().map(DropType::getId).toList().contains(typeId)) {
            return true;
        }
        return mobTypes.containsKey(typeId);
    }

    @Override
    public List<DropType> getDefaultTypes() {
        return DEFAULT_TYPES;
    }

    public List<String> getAllTypesIds() {
        return allTypesIds;
    }
}
