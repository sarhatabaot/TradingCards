package net.tinetwork.tradingcards.tradingcardsplugin.utils;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author sarhatabaot
 */
class CardUtilTest {

    @Test
    void calculateIfShiny() {
        assertFalse(CardUtil.calculateIfShiny(5467, 1000, false));
        assertTrue(CardUtil.calculateIfShiny(10, 1000, false));

        assertTrue(CardUtil.calculateIfShiny(100,1000,true));
        assertTrue(CardUtil.calculateIfShiny(1000,1000,true));
        assertTrue(CardUtil.calculateIfShiny(10000,1000,true));
    }

    @Test
    void shouldDrop() {
        assertTrue(CardUtil.shouldDrop(10,1000));
        assertTrue(CardUtil.shouldDrop(1000,1000));
        assertFalse(CardUtil.shouldDrop(1001,1000));
    }
}