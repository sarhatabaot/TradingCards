package net.tinetwork.tradingcards;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PrefixNamingStrategyTest {

    private final PrefixNamingStrategy strategy = new PrefixNamingStrategy();

    @Test
    void testReplacePrefix_EncodedPrefix() {
        assertEquals("custom_types", strategy.replacePrefix("_7bprefix_7dcustom_types"));
        assertEquals("upgrades_result", strategy.replacePrefix("_7bprefix_7dupgrades_result"));
        assertEquals("decks", strategy.replacePrefix("_7bprefix_7ddecks"));
    }

    @Test
    void testReplacePrefix_VariousEncodedPrefixes() {
        assertEquals("table", strategy.replacePrefix("_7bPREFIX_7dtable"));
        assertEquals("table", strategy.replacePrefix("_7btablePrefix_7dtable"));
    }

    @Test
    void testReplacePrefix_DollarBracePrefix() {
        assertEquals("custom_types", strategy.replacePrefix("${prefix}custom_types"));
        assertEquals("table", strategy.replacePrefix("${PREFIX}table"));
    }

    @Test
    void testReplacePrefix_GenericFallbacks() {
        assertEquals("custom_types", strategy.replacePrefix("_7bunknown_7dcustom_types"));
        assertEquals("table", strategy.replacePrefix("${unknown}table"));
    }

    @Test
    void testReplacePrefix_NoPrefix() {
        assertEquals("normal_table", strategy.replacePrefix("normal_table"));
    }

    @Test
    void testReplacePrefix_ComplexCase() {
        // Test a case similar to the generated file
        assertEquals("upgrades_result", strategy.replacePrefix("_7bprefix_7dupgrades_result"));
    }
}
