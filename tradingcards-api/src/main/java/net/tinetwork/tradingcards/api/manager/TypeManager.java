package net.tinetwork.tradingcards.api.manager;

import net.tinetwork.tradingcards.api.model.DropType;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Map;

/**
 * @author sarhatabaot
 */
public interface TypeManager {
    DropType getType(final String type);
    void loadTypes();
    Map<String, DropType> getTypes();

    DropType getMobType(final EntityType type);
    boolean containsType(final String typeId);
    List<DropType> getDefaultTypes();
}
