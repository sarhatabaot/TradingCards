package net.tinetwork.tradingcards.tradingcardsplugin.commands.edit;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
public enum EditSeries implements Edit{
    DISPLAY_NAME,
    MODE,
    COLORS;


    @Contract(pure = true)
    @Override
    public @NotNull String editName() {
        return "series";
    }
}
