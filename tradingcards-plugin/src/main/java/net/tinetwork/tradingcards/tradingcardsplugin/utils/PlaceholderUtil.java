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

    public static final String PLAYER = "%player%";
    public static final String PACK = "%pack%";
    public static final String CARD = "%card%";
    public static final String NAME = "%name%";
    public static final String UUID = "%uuid%";
    public static final String RARITY = "%rarity%";
    public static final String BUY_AMOUNT = "%buyAmount%";
    public static final String SELL_AMOUNT = "%sellAmount%";
    public static final String AMOUNT = "%amount%";

    @Contract(pure = true)
    public static @NotNull String matchAllAsRegEx(final String string) {
        return "(?i)"+string;
    }
}
