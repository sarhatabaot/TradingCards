package net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate;

import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.ConfigurateException;

import java.util.List;

/**
 * @author sarhatabaot
 */
public class RarityMigratorBukkitRunnable extends MigratorBukkitRunnable{
    public RarityMigratorBukkitRunnable(final TradingCards plugin, final CommandSender sender, final Storage<TradingCard> source) {
        super(plugin, sender, source);
    }

    @Override
    public void onExecute() throws ConfigurateException {
        int totalRarities = source.getRarities().size();
        Util.logAndMessage(sender,"Found "+totalRarities +" rarities.");
        for(final Rarity rarity: source.getRarities()) {
            Util.logAndMessage(sender,"Started conversion for "+rarity.getName());
            plugin.getStorage().createRarity(rarity.getName());
            plugin.getStorage().editRaritySellPrice(rarity.getName(),rarity.sellPrice());
            plugin.getStorage().editRarityBuyPrice(rarity.getName(),rarity.buyPrice());
            plugin.getStorage().editRarityDefaultColor(rarity.getName(), rarity.getDefaultColor());
            plugin.getStorage().editRarityDisplayName(rarity.getName(),rarity.getDisplayName());

            List<String> rewards = rarity.getRewards();
            for(String reward: rewards) {
                plugin.getStorage().editRarityAddReward(rarity.getName(),reward);
            }
        }
        Util.logAndMessage(sender, "&2Finished conversion of " + totalRarities + " rarities.");
    }
}
