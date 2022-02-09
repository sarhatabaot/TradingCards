package net.tinetwork.tradingcards.tradingcardsplugin.card;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author sarhatabaot
 */
public class EmptyCard extends TradingCard {
    public static final ItemStack AIR = new ItemStack(Material.AIR);

    public EmptyCard() {
        super("nullCard");
    }

    @Override
    public EmptyCard get() {
        return this;
    }

    @Override
    public ItemStack buildItem(boolean shiny) {
        return AIR;
    }

}
