package net.tinetwork.tradingcards;

import be.seeseemelk.mockbukkit.MockBukkit;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.junit.jupiter.api.*;

class TestPluginClass {
    private static TradingCards plugin;

    @BeforeAll
    public static void load() {
        MockBukkit.mock();
        plugin = MockBukkit.load(TradingCards.class);
    }

    @AfterAll
    public static void unload(){
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Verify the configs were loaded")
    void testConfigs() {
        Assertions.assertNotNull(plugin.getGeneralConfig());
        Assertions.assertNotNull(plugin.getChancesConfig());
        Assertions.assertNotNull(plugin.getRaritiesConfig());
        Assertions.assertNotNull(plugin.getMessagesConfig());
        Assertions.assertNotNull(plugin.getPacksConfig());

        Assertions.assertNotNull(plugin.getCardsConfig());
        Assertions.assertNotNull(plugin.getDeckConfig());
    }

    @Test
    @DisplayName("Make sure lists were loaded.")
    void testGetters() {
        Assertions.assertNotNull(plugin.getPlayerBlacklist());
        Assertions.assertNotNull(plugin.getWorldBlacklist());
    }
}
