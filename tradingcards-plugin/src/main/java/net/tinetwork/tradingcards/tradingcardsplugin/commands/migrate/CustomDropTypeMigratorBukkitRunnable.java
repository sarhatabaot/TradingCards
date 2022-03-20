package net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate;

import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.ConfigurateException;

/**
 * @author sarhatabaot
 */
public class CustomDropTypeMigratorBukkitRunnable extends MigratorBukkitRunnable{
    public CustomDropTypeMigratorBukkitRunnable(final TradingCards plugin, final CommandSender sender, final Storage<TradingCard> source) {
        super(plugin, sender, source);
    }

    @Override
    public void onExecute() throws ConfigurateException {
        int totalTypes = source.getDropTypes().size();
        Util.logAndMessage(sender,"Found "+totalTypes + " types.");
        for(DropType dropType: source.getDropTypes()) {
            plugin.getStorage().createCustomType(dropType.getId(),dropType.getType());
            plugin.getStorage().editCustomTypeDisplayName(dropType.getId(),dropType.getDisplayName());
        }
        Util.logAndMessage(sender, "&2Finished conversion of " + totalTypes + " types.");
    }
}
