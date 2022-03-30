package net.tinetwork.tradingcards.tradingcardsplugin.utils;

import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author sarhatabaot
 */
class MobGroupUtilTest {

    @Test
    void isMobHostile() {
        assertTrue(MobGroupUtil.isMobHostile(EntityType.BLAZE));
        assertFalse(MobGroupUtil.isMobHostile(EntityType.BEE));
    }

    @Test
    void isMobNeutral() {
        assertTrue(MobGroupUtil.isMobNeutral(EntityType.DOLPHIN));
        assertFalse(MobGroupUtil.isMobNeutral(EntityType.BLAZE));
    }

    @Test
    void isMobPassive() {
        assertTrue(MobGroupUtil.isMobPassive(EntityType.SQUID));
        assertFalse(MobGroupUtil.isMobPassive(EntityType.BLAZE));
    }

    @Test
    void isMobBoss() {
        assertTrue(MobGroupUtil.isMobBoss(EntityType.ENDER_DRAGON));
        assertFalse(MobGroupUtil.isMobBoss(EntityType.BLAZE));
    }

    @Test
    @DisplayName("isMob(EntityType)")
    void isMobType() {
        assertTrue(MobGroupUtil.isMob(EntityType.ENDER_DRAGON));
        assertTrue(MobGroupUtil.isMob(EntityType.BLAZE));
        assertTrue(MobGroupUtil.isMob(EntityType.SQUID));
        assertTrue(MobGroupUtil.isMob(EntityType.DOLPHIN));
        assertFalse(MobGroupUtil.isMob(EntityType.SNOWBALL));
    }
    @Test
    @DisplayName("isMob(String)")
    void isMobString() {
        assertTrue(MobGroupUtil.isMob("BLAZE"));
        assertFalse(MobGroupUtil.isMob("SNOWBALL"));
        assertFalse(MobGroupUtil.isMob("Test"));
    }
}