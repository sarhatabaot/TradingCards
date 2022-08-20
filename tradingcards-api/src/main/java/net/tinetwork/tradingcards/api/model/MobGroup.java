package net.tinetwork.tradingcards.api.model;

import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

/**
 * @author sarhatabaot
 */
public class MobGroup {
    private final String id;
    private String displayName;
    private final Set<EntityType> entityTypes;

    private Set<MobGroup> groups;

    public MobGroup(final String id, final Set<EntityType> entityTypes) {
        this.id = id;
        this.displayName = id;
        this.entityTypes = entityTypes;
    }

    public MobGroup(final String id, final String displayName, final Set<EntityType> entityTypes) {
        this.id = id;
        this.displayName = displayName;
        this.entityTypes = entityTypes;
        this.groups = Collections.emptySet();
    }

    public MobGroup(final String id, final String displayName, final Set<EntityType> entityTypes, final Set<MobGroup> groups) {
        this.id = id;
        this.displayName = displayName;
        this.entityTypes = entityTypes;
        this.groups = groups;
    }


    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Set<EntityType> getEntityTypes() {
        return entityTypes;
    }

    public Set<MobGroup> getGroups() {
        return groups;
    }

    public void addGroup(final @NotNull MobGroup group) {
        if (id.equals(group.getId())) {
            return;
        }

        groups.add(group);
    }

    public void removeGroup(final MobGroup group) {
        groups.remove(group);
    }

    public void addEntityType(final EntityType type) {
        entityTypes.add(type);
    }

    public void removeEntityType(final EntityType type) {
        entityTypes.remove(type);
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
}
