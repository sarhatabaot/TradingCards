package net.tinetwork.tradincards.tradincardsplugin.card;

import net.tinetwork.tradincards.tradincardsplugin.TradingCards;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author sarhatabaot
 */
public class NullCard extends TradingCard {
    public static ItemStack air = new ItemStack(Material.AIR);

    public NullCard(TradingCards plugin) {
        super(plugin, "nullCard");
    }


    @Override
    public NullCard get() {
        return this;
    }

    @Override
    public ItemStack buildItem() {
        return air;
    }

}
