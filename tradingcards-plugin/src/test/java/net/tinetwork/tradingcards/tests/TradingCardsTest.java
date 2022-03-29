package net.tinetwork.tradingcards.tests;

import be.seeseemelk.mockbukkit.MockBukkit;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author sarhatabaot
 */
class TradingCardsTest {
    private static Server server;
    private static TradingCards plugin;

    @BeforeAll
    static void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(TradingCards.class);
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Verify configs have loaded.")
    void verifyConfigs() {
        assertNotNull(plugin.getChancesConfig());
        assertNotNull(plugin.getGeneralConfig());
        assertNotNull(plugin.getMessagesConfig());
        assertNotNull(plugin.getStorage());
    }

    @Test
    @DisplayName("Verify managers have loaded.")
    void verifyManagers() {
        assertNotNull(plugin.getCardManager());
        assertNotNull(plugin.getDeckManager());
        assertNotNull(plugin.getPackManager());
        assertNotNull(plugin.getDropTypeManager());
        assertNotNull(plugin.getSeriesManager());
        assertNotNull(plugin.getRarityManager());
    }

    @Test
    void isMobHostile() {
        assertTrue(plugin.isMobHostile(EntityType.BLAZE));
        assertFalse(plugin.isMobHostile(EntityType.BEE));
    }

    @Test
    void isMobNeutral() {
        assertTrue(plugin.isMobNeutral(EntityType.DOLPHIN));
        assertFalse(plugin.isMobNeutral(EntityType.BLAZE));
    }

    @Test
    void isMobPassive() {
        assertTrue(plugin.isMobPassive(EntityType.SQUID));
        assertFalse(plugin.isMobPassive(EntityType.BLAZE));
    }

    @Test
    void isMobBoss() {
        assertTrue(plugin.isMobBoss(EntityType.ENDER_DRAGON));
        assertFalse(plugin.isMobBoss(EntityType.BLAZE));
    }

    @Test
    void isMobType() {
        assertTrue(plugin.isMob(EntityType.ENDER_DRAGON));
        assertFalse(plugin.isMob(EntityType.SNOWBALL));
    }

    @Test
    void isMobString() {
        assertTrue(plugin.isMob("BLAZE"));
        assertFalse(plugin.isMob("SNOWBALL"));
    }


}