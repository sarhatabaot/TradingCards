package net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.spongepowered.configurate.ConfigurateException;

/**
 * @author sarhatabaot
 */
public abstract class MigratorBukkitRunnable extends BukkitRunnable {
    protected final TradingCards plugin;
    protected final CommandSender sender;
    protected final Storage<TradingCard> source;

    protected MigratorBukkitRunnable(final TradingCards plugin, final CommandSender sender, final Storage<TradingCard> source) {
        this.plugin = plugin;
        this.sender = sender;
        this.source = source;
    }

    @Override
    public void run() {
        long startTime = System.nanoTime();
        try {
            Util.logAndMessage(sender, "Found " + getTotalAmount() + " " + getConversionType());
            onExecute();
            Util.logAndMessage(sender, "&2Finished conversion of " + getTotalAmount() + " " + getConversionType());
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000;
            sender.sendMessage(ChatUtil.color("&aTook a total of " + duration + "ms"));
        } catch (ConfigurateException e) {
            sender.sendMessage("There was a problem accessing the yaml data. Check your console for more info.");
            Util.logSevereException(e);
        } catch (Exception e) {
            sender.sendMessage("There was an error. Check your console for more info.");
            Util.logSevereException(e);
        }
    }

    public abstract void onExecute() throws ConfigurateException;

    public abstract String getConversionType();

    public abstract int getTotalAmount();

}
