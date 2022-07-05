package net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate;

import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
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
        for(final Rarity rarity: source.getRarities()) {
            Util.logAndMessage(sender, InternalMessages.STARTED_CONVERSION_FOR.formatted(rarity.getId()));
            plugin.getStorage().createRarity(rarity.getId());
            plugin.getStorage().editRaritySellPrice(rarity.getId(),rarity.getSellPrice());
            plugin.getStorage().editRarityBuyPrice(rarity.getId(),rarity.getBuyPrice());
            plugin.getStorage().editRarityDefaultColor(rarity.getId(), rarity.getDefaultColor());
            plugin.getStorage().editRarityDisplayName(rarity.getId(),rarity.getDisplayName());

            List<String> rewards = rarity.getRewards();
            for(String reward: rewards) {
                plugin.getStorage().editRarityAddReward(rarity.getId(),reward);
            }
        }
    }

    @Override
    public String getMigrationType() {
        return "rarities";
    }

    @Override
    public int getTotalAmount() {
        return source.getRarities().size();
    }
}
