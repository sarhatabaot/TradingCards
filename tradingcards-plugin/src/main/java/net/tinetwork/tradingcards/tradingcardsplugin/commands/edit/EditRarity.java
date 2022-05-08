package net.tinetwork.tradingcards.tradingcardsplugin.commands.edit;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
public enum EditRarity implements Edit{
    DISPLAY_NAME,
    DEFAULT_COLOR,
    BUY_PRICE,
    SELL_PRICE,
    ADD_REWARD,
    REMOVE_REWARD,
    REMOVE_ALL_REWARDS,
    CUSTOM_ORDER;


    @Contract(pure = true)
    @Override
    public @NotNull String editName() {
        return "rarity";
    }
}
