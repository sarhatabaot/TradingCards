package net.tinetwork.tradingcards.tradingcardsplugin;

import net.tinetwork.tradingcards.api.model.MobGroup;
import org.bukkit.entity.EntityType;

import java.util.Set;

/**
 * @author sarhatabaot
 */
public enum DefaultMobGroups {
    PASSIVE(new MobGroup("passive", Set.of(EntityType.DONKEY, EntityType.MULE, EntityType.SKELETON_HORSE, EntityType.CHICKEN, EntityType.COW,
            EntityType.SQUID, EntityType.TURTLE, EntityType.TROPICAL_FISH, EntityType.PUFFERFISH, EntityType.SHEEP, EntityType.PIG,
            EntityType.PHANTOM, EntityType.SALMON, EntityType.COD, EntityType.RABBIT, EntityType.VILLAGER, EntityType.BAT,
            EntityType.PARROT, EntityType.HORSE, EntityType.WANDERING_TRADER, EntityType.CAT, EntityType.MUSHROOM_COW, EntityType.TRADER_LLAMA))),
    NEUTRAL(new MobGroup("neutral", Set.of(EntityType.ENDERMAN, EntityType.POLAR_BEAR, EntityType.LLAMA, EntityType.WOLF,
            EntityType.DOLPHIN, EntityType.SNOWMAN, EntityType.IRON_GOLEM, EntityType.BEE, EntityType.PANDA, EntityType.FOX))),
    HOSTILE(new MobGroup("hostile", Set.of(EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER,
            EntityType.BLAZE, EntityType.SILVERFISH, EntityType.GHAST, EntityType.SLIME, EntityType.EVOKER, EntityType.VINDICATOR,
            EntityType.VEX, EntityType.SHULKER, EntityType.GUARDIAN, EntityType.MAGMA_CUBE, EntityType.ELDER_GUARDIAN, EntityType.STRAY,
            EntityType.HUSK, EntityType.DROWNED, EntityType.WITCH, EntityType.ZOMBIE_VILLAGER, EntityType.ENDERMITE, EntityType.PILLAGER, EntityType.RAVAGER,
            EntityType.HOGLIN, EntityType.PIGLIN, EntityType.STRIDER, EntityType.ZOGLIN, EntityType.ZOMBIFIED_PIGLIN, EntityType.WITHER_SKELETON))),
    BOSS(new MobGroup("boss", Set.of(EntityType.ENDER_DRAGON, EntityType.WITHER)));
    private final MobGroup group;

    DefaultMobGroups(final MobGroup group) {
        this.group = group;
    }

    public MobGroup getGroup() {
        return group;
    }

    public static boolean isMob(EntityType type) {
        for(DefaultMobGroups defaultMobGroups: values()) {
            if(defaultMobGroups.getGroup().getEntities().contains(type))
                return true;
        }
        return false;
    }

    public boolean hasMobType(EntityType type) {
        return group.getEntities().contains(type);
    }
}