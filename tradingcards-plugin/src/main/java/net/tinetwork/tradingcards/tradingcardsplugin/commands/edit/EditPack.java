package net.tinetwork.tradingcards.tradingcardsplugin.commands.edit;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
public enum EditPack implements Edit{
    PRICE,
    PERMISSION,
    DISPLAY_NAME,
    CONTENTS;

    @Contract(pure = true)
    @Override
    public @NotNull String editName() {
        return "pack";
    }
}
