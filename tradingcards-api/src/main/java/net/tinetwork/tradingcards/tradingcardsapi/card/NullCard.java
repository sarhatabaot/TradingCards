package net.tinetwork.tradingcards.tradingcardsapi.card;

import net.tinetwork.tradingcards.tradingcardsapi.TradingCardsPlugin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author sarhatabaot
 */
public class NullCard extends Card {
    public static ItemStack air = new ItemStack(Material.AIR);

    public NullCard(final TradingCardsPlugin plugin) {
        super("nullCard");
    }

    @Override
    public ItemStack buildItem() {
        return air;
    }

}
