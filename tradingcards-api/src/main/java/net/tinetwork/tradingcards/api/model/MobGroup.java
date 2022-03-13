package net.tinetwork.tradingcards.api.model;

import org.bukkit.entity.EntityType;

import java.util.Set;

/**
 * @author sarhatabaot
 */
public class MobGroup {
    private final String id;
    private final Set<EntityType> entities;

    public MobGroup(final String id, final Set<EntityType> entities) {
        this.id = id;
        this.entities = entities;
    }

    public Set<EntityType> getEntities() {
        return entities;
    }

    public String getId() {
        return id;
    }
}
