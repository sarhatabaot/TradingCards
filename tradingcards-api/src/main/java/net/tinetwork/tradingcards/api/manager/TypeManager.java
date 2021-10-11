package net.tinetwork.tradingcards.api.manager;

import net.tinetwork.tradingcards.api.exceptions.UnsupportedDropTypeException;
import net.tinetwork.tradingcards.api.model.DropType;
import org.bukkit.entity.EntityType;

import java.util.Map;

/**
 * @author sarhatabaot
 */
public interface TypeManager {
    DropType getType(final String type) throws UnsupportedDropTypeException;
    void loadTypes();
    Map<String, DropType> getTypes();

    DropType getMobType(EntityType type);
}
