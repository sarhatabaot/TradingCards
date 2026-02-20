package net.tinetwork.tradingcards.tradingcardsplugin.drop;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public final class MobDropPool {
    private final EntityType entityType;
    private final @Nullable String nameCheck;
    private final boolean enabled;
    private final int dropMin;
    private final int dropMax;
    private final int dropChance;
    private final List<DropPoolEntry> entries;
    private final long totalWeight;

    public MobDropPool(
            final EntityType entityType,
            final @Nullable String nameCheck,
            final boolean enabled,
            final int dropMin,
            final int dropMax,
            final int dropChance,
            final List<DropPoolEntry> entries
    ) {
        this.entityType = entityType;
        this.nameCheck = normalizeName(nameCheck);
        this.enabled = enabled;
        this.dropMin = Math.max(0, dropMin);
        this.dropMax = Math.max(this.dropMin, dropMax);
        this.dropChance = dropChance;
        this.entries = Collections.unmodifiableList(entries.stream().filter(entry -> entry.weight() > 0).toList());
        this.totalWeight = this.entries.stream().mapToLong(DropPoolEntry::weight).sum();
    }

    public EntityType entityType() {
        return entityType;
    }

    public @Nullable String nameCheck() {
        return nameCheck;
    }

    public boolean enabled() {
        return enabled;
    }

    public int dropMin() {
        return dropMin;
    }

    public int dropMax() {
        return dropMax;
    }

    public int dropChance() {
        return dropChance;
    }

    public List<DropPoolEntry> entries() {
        return entries;
    }

    public boolean hasCustomDropChance() {
        return this.dropChance >= 0;
    }

    public boolean hasEntries() {
        return !entries.isEmpty() && totalWeight > 0;
    }

    public boolean matches(@NotNull LivingEntity entity) {
        if (!enabled || entity.getType() != entityType) {
            return false;
        }
        return matchesName(entity.getCustomName());
    }

    public boolean matchesName(final @Nullable String displayName) {
        if (nameCheck == null) {
            return true;
        }

        if (displayName == null || displayName.isBlank()) {
            return false;
        }

        return nameCheck.equals(displayName.trim()) || nameCheck.equals(ChatColor.stripColor(displayName).trim());
    }

    public int getDropAmount(final @NotNull Random random) {
        if (dropMax <= dropMin) {
            return dropMin;
        }
        return random.nextInt((dropMax - dropMin) + 1) + dropMin;
    }

    public @NotNull Optional<DropPoolEntry> getRandomEntry(final @NotNull Random random) {
        if (!hasEntries()) {
            return Optional.empty();
        }

        final long roll = random.nextLong(totalWeight) + 1;
        return getEntryForRoll(roll);
    }

    public @NotNull Optional<DropPoolEntry> getEntryForRoll(final long roll) {
        if (!hasEntries() || roll <= 0 || roll > totalWeight) {
            return Optional.empty();
        }

        long cumulativeWeight = 0;
        for (DropPoolEntry entry : entries) {
            cumulativeWeight += entry.weight();
            if (roll <= cumulativeWeight) {
                return Optional.of(entry);
            }
        }

        return Optional.empty();
    }

    private @Nullable String normalizeName(final @Nullable String rawName) {
        if (rawName == null) {
            return null;
        }

        final String trimmed = rawName.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        return trimmed;
    }
}
