package net.tinetwork.tradingcards.tradingcardsplugin.commands.edit;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum EditCard implements Edit{
    DISPLAY_NAME,
    CUSTOM_MODEL_DATA,
    BUY_PRICE,
    SELL_PRICE,
    INFO,
    SERIES,
    HAS_SHINY,
    TYPE,
    CURRENCY_ID;


    @Contract(pure = true)
    @Override
    public @NotNull String editName() {
        return "card";
    }
}
