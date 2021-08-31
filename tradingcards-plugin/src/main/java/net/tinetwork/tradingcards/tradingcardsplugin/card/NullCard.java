package net.tinetwork.tradingcards.tradingcardsplugin.card;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author sarhatabaot
 */
public class NullCard extends TradingCard {
    public static final ItemStack AIR = new ItemStack(Material.AIR);

    public NullCard() {
        super("nullCard");
    }

    @Override
    public NullCard get() {
        return this;
    }

    @Override
    public ItemStack buildItem() {
        return AIR;
    }

}
