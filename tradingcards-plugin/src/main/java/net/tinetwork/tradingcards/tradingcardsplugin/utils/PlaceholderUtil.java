package net.tinetwork.tradingcards.tradingcardsplugin.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sarhatabaot
 */
public class PlaceholderUtil {
    private PlaceholderUtil() {
        throw new UnsupportedOperationException();
    }

    public static final String PLAYER = "%player%";
    public static final String PACK = "%pack%";
    public static final String CARD = "%card%";
    public static final String NAME = "%name%";
    public static final String UUID = "%uuid%";
    public static final String RARITY = "%rarity%";
    public static final String BUY_AMOUNT = "%buyamount%";
    public static final String SELL_AMOUNT = "%sellamount%";
    public static final String PREFIX = "%prefix%";
    public static final String COLOR = "%color%";
    public static final String BUY_PRICE = "%buy_price%";
    public static final String SELL_PRICE = "%sell_price%";
    public static final String SHINY_PREFIX = "%shiny_prefix%";
    public static final String SHINY_PREFIX_ALT = "%shinyprefix%";
    public static final String AMOUNT = "%amount%";

    @Contract(pure = true)
    public static @NotNull String matchAllAsRegEx(final String string) {
        return "(?i)"+string;
    }
}
