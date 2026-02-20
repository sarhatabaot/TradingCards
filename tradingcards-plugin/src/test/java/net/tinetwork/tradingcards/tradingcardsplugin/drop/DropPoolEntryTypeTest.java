package net.tinetwork.tradingcards.tradingcardsplugin.drop;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DropPoolEntryTypeTest {
    @Test
    void parseConfigKeyCard() {
        DropPoolEntryType.ParsedDropPoolKey parsed = DropPoolEntryType.parseConfigKey("card_zombie");
        assertEquals(DropPoolEntryType.CARD, parsed.type());
        assertEquals("zombie", parsed.id());
    }

    @Test
    void parseConfigKeyRarityCaseInsensitivePrefix() {
        DropPoolEntryType.ParsedDropPoolKey parsed = DropPoolEntryType.parseConfigKey("Rarity_common");
        assertEquals(DropPoolEntryType.RARITY, parsed.type());
        assertEquals("common", parsed.id());
    }

    @Test
    void parseConfigKeyInvalid() {
        assertNull(DropPoolEntryType.parseConfigKey("pack_starter"));
    }
}
