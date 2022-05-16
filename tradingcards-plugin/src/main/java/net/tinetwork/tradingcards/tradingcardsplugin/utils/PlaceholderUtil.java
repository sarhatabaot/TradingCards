package net.tinetwork.tradingcards.tradingcardsplugin.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
public class PlaceholderUtil {
    private PlaceholderUtil() {
        throw new UnsupportedOperationException();
    }

    public static final InternalPlaceholder PLAYER = new InternalPlaceholder("%player%");
    public static final InternalPlaceholder PACK = new InternalPlaceholder("%pack%");
    public static final InternalPlaceholder CARD = new InternalPlaceholder("%card%");
    public static final InternalPlaceholder DISPLAY_NAME = new InternalPlaceholder("%name%");
    public static final InternalPlaceholder UUID = new InternalPlaceholder("%uuid%");
    public static final InternalPlaceholder RARITY = new InternalPlaceholder("%rarity%");
    public static final InternalPlaceholder SERIES = new InternalPlaceholder("%series%");
    public static final InternalPlaceholder BUY_AMOUNT = new InternalPlaceholder("%buyamount%");
    public static final InternalPlaceholder SELL_AMOUNT = new InternalPlaceholder("%sellamount%");
    public static final InternalPlaceholder PREFIX = new InternalPlaceholder("%prefix%");
    public static final InternalPlaceholder COLOR = new InternalPlaceholder("%color%");
    public static final InternalPlaceholder BUY_PRICE = new InternalPlaceholder("%buy_price%");
    public static final InternalPlaceholder SELL_PRICE = new InternalPlaceholder("%sell_price%");
    public static final InternalPlaceholder SHINY_PREFIX = new InternalPlaceholder("%shiny_prefix%");
    public static final InternalPlaceholder SHINY_PREFIX_ALT = new InternalPlaceholder("%shinyprefix%");
    public static final InternalPlaceholder AMOUNT = new InternalPlaceholder("%amount%");
    public static final InternalPlaceholder CARDS_OWNED = new InternalPlaceholder("%cards_owned%");
    public static final InternalPlaceholder SHINY_CARDS_OWNED = new InternalPlaceholder("%shiny_cards_owned%");
    public static final InternalPlaceholder CARDS_TOTAL = new InternalPlaceholder("%cards_total%");

    public record InternalPlaceholder(String placeholder) {
        @Contract(pure = true)
        public @NotNull String asRegex() {
            return "(?i)" + placeholder;
        }
    }
}
