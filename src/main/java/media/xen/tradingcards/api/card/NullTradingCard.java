package media.xen.tradingcards.api.card;

import media.xen.tradingcards.TradingCards;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author sarhatabaot
 */
public class NullTradingCard extends TradingCard{
    public static ItemStack air = new ItemStack(Material.AIR);

    public NullTradingCard(final TradingCards plugin) {
        super(plugin, "nullCard");
    }

    @Override
    public ItemStack build() {
        return air;
    }
}
