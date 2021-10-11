package net.tinetwork.tradingcards.tradingcardsplugin.managers;


import net.tinetwork.tradingcards.api.exceptions.UnsupportedDropTypeException;
import net.tinetwork.tradingcards.api.manager.TypeManager;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class DropTypeManager implements TypeManager {
    private final TradingCards plugin;
    public static final DropType HOSTILE = new DropType("hostile","Hostile","hostile");
    public static final DropType NEUTRAL = new DropType("neutral", "Neutral", "neutral");
    public static final DropType PASSIVE = new DropType("passive", "Passive", "passive");
    public static final DropType BOSS = new DropType("boss", "Boss", "boss");

    private Map<String, DropType> mobTypes;

    public DropTypeManager(final TradingCards plugin) {
        this.plugin = plugin;
        loadTypes();
    }

    @Override
    public DropType getType(final String type) throws UnsupportedDropTypeException {
        DropType dropType = mobTypes.get(type);
        if(dropType == null) {
            return switch (type.toLowerCase()) {
                case "boss" -> BOSS;
                case "hostile" -> HOSTILE;
                case "neutral" -> NEUTRAL;
                case "passive" -> PASSIVE;
                default -> throw new UnsupportedDropTypeException();
            };
        }
        return dropType;
    }

    @Override
    public void loadTypes() {
        this.mobTypes = new HashMap<>();
        for(DropType dropType: plugin.getDropTypesConfig().getDropTypes()) {
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
}
