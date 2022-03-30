package net.tinetwork.tradingcards.tradingcardsplugin.utils;

import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author sarhatabaot
 */
public class MobGroupUtil {
    private MobGroupUtil() {
        throw new UnsupportedOperationException();
    }

    private static final Set<EntityType> hostile = ImmutableSet.<EntityType>builder().add(EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER,
            EntityType.BLAZE, EntityType.SILVERFISH, EntityType.GHAST, EntityType.SLIME, EntityType.EVOKER, EntityType.VINDICATOR,
            EntityType.VEX, EntityType.SHULKER, EntityType.GUARDIAN, EntityType.MAGMA_CUBE, EntityType.ELDER_GUARDIAN, EntityType.STRAY,
            EntityType.HUSK, EntityType.DROWNED, EntityType.WITCH, EntityType.ZOMBIE_VILLAGER, EntityType.ENDERMITE, EntityType.PILLAGER, EntityType.RAVAGER,
            EntityType.HOGLIN, EntityType.PIGLIN, EntityType.STRIDER, EntityType.ZOGLIN, EntityType.ZOMBIFIED_PIGLIN, EntityType.WITHER_SKELETON).build();
    private static final Set<EntityType> neutral = ImmutableSet.<EntityType>builder().add(EntityType.ENDERMAN, EntityType.POLAR_BEAR, EntityType.LLAMA, EntityType.WOLF,
            EntityType.DOLPHIN, EntityType.SNOWMAN, EntityType.IRON_GOLEM, EntityType.BEE, EntityType.PANDA, EntityType.FOX).build();
    private static final Set<EntityType> passive = ImmutableSet.<EntityType>builder().add(EntityType.DONKEY, EntityType.MULE, EntityType.SKELETON_HORSE, EntityType.CHICKEN, EntityType.COW,
            EntityType.SQUID, EntityType.TURTLE, EntityType.TROPICAL_FISH, EntityType.PUFFERFISH, EntityType.SHEEP, EntityType.PIG,
            EntityType.PHANTOM, EntityType.SALMON, EntityType.COD, EntityType.RABBIT, EntityType.VILLAGER, EntityType.BAT,
            EntityType.PARROT, EntityType.HORSE, EntityType.WANDERING_TRADER, EntityType.CAT, EntityType.MUSHROOM_COW, EntityType.TRADER_LLAMA).build();
    private static final Set<EntityType> boss = ImmutableSet.<EntityType>builder().add(EntityType.ENDER_DRAGON, EntityType.WITHER).build();

    public static Set<EntityType> getHostile() {
        return hostile;
    }

    public static Set<EntityType> getNeutral() {
        return neutral;
    }

    public static Set<EntityType> getPassive() {
        return passive;
    }

    public static Set<EntityType> getBoss() {
        return boss;
    }

    public static boolean isMob(EntityType type) {
        return hostile.contains(type) || neutral.contains(type) || passive.contains(type) || boss.contains(type);
    }

    public static boolean isMobHostile(EntityType e) {
        return hostile.contains(e);
    }

    public static boolean isMobNeutral(EntityType e) {
        return neutral.contains(e);
    }

    public static boolean isMobPassive(EntityType e) {
        return passive.contains(e);
    }

    public static boolean isMobBoss(EntityType e) {
        return boss.contains(e);
    }

    public static boolean isMob(@NotNull String input) {
        try {
            EntityType type = EntityType.valueOf(input.toUpperCase());
            return isMob(type);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
