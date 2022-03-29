package net.tinetwork.tradingcards.tradingcardsplugin.commands.edit;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
public enum EditType implements Edit{
    TYPE,
    DISPLAY_NAME;


    @Contract(pure = true)
    @Override
    public @NotNull String editName() {
        return "custom type";
    }
}
