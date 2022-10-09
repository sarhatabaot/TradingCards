package net.tinetwork.tradingcards.tradingcardsplugin.commands.edit;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
public enum EditUpgrade implements Edit{
    REQUIRED,
    RESULT;

    @Contract(pure = true)
    @Override
    public @NotNull String editName() {
        return "upgrade";
    }
}
