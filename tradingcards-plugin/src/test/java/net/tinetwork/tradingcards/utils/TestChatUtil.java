package net.tinetwork.tradingcards.utils;

import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TestChatUtil {

    @Test
    @DisplayName("Test ChatUtil.stripAllColor(...)")
    void testStripColors() {
        final String expected = "Hello World";
        Assertions.assertEquals(expected, ChatUtil.stripAllColor("&6&lHello &4World"));
        Assertions.assertEquals(expected, ChatUtil.stripAllColor(ChatColor.GOLD + "" + ChatColor.BOLD + "Hello " + ChatColor.RED + "World"));
    }
}
