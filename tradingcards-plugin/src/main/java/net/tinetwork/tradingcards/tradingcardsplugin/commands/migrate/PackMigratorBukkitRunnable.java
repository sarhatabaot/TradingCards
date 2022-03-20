package net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate;

import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.ConfigurateException;

/**
 * @author sarhatabaot
 */
public class PackMigratorBukkitRunnable extends MigratorBukkitRunnable{
    public PackMigratorBukkitRunnable(final TradingCards plugin, final CommandSender sender, final Storage<TradingCard> source) {
        super(plugin, sender, source);
    }

    @Override
    public void onExecute() throws ConfigurateException {
        int totalPacks = source.getPacks().size();
        Util.logAndMessage(sender,"Found "+totalPacks+" packs.");
        for(Pack pack: source.getPacks()) {
            Util.logAndMessage(sender,"Started conversion for "+pack.id());
            plugin.getStorage().createPack(pack.id());
            plugin.getStorage().editPackPrice(pack.id(),pack.price());
            plugin.getStorage().editPackPermission(pack.id(),pack.getPermissions());
            plugin.getStorage().editPackDisplayName(pack.id(), pack.getDisplayName());

            for(final Pack.PackEntry entry:pack.getPackEntryList()){
                plugin.getStorage().editPackContentsAdd(pack.id(),entry);
            }
        }
        Util.logAndMessage(sender, "&2Finished conversion of " + totalPacks + " packs.");
    }
}
