package net.tinetwork.tradingcards.api.manager;

import net.tinetwork.tradingcards.api.model.DropType;

import java.util.Map;

/**
 * @author sarhatabaot
 */
public interface TypeManager {
    DropType getType(final String type);
    void loadTypes();
    Map<String, DropType> getTypes();
}
