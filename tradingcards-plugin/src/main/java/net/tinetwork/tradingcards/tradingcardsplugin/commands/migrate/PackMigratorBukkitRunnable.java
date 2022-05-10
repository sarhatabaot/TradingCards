package net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate;

import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
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
    public String getMigrationType() {
        return "packs";
    }

    @Override
    public int getTotalAmount() {
        return source.getPacks().size();
    }

    @Override
    public void onExecute() throws ConfigurateException {
        for(Pack pack: source.getPacks()) {
            Util.logAndMessage(sender, InternalMessages.STARTED_CONVERSION_FOR.formatted(pack.id()));
            plugin.getStorage().createPack(pack.id());
            plugin.getStorage().editPackPrice(pack.id(),pack.getBuyPrice());
            plugin.getStorage().editPackPermission(pack.id(),pack.getPermission());
            plugin.getStorage().editPackDisplayName(pack.id(), pack.getDisplayName());

            for(final Pack.PackEntry entry:pack.getPackEntryList()){
                plugin.getStorage().editPackContentsAdd(pack.id(),entry);
            }
        }
    }
}
