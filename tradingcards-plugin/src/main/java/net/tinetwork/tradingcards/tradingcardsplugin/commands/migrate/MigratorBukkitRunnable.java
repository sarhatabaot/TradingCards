package net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.LoggerUtil;
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
            if(getTotalAmount() != 0) { //because decks have to be different, change so this isn't needed todo
                LoggerUtil.logAndMessage(sender, "Found " + getTotalAmount() + " " + getMigrationType());
            }
            onExecute();
            if(getTotalAmount() != 0) { //because decks have to be different, change so this isn't needed todo
                LoggerUtil.logAndMessage(sender, "&2Finished conversion of " + getTotalAmount() + " " + getMigrationType());
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000;
            sender.sendMessage(ChatUtil.color("&aTook a total of " + duration + "ms"));
        } catch (ConfigurateException e) {
            sender.sendMessage("There was a problem accessing the yaml data. Check your console for more info.");
            LoggerUtil.logSevereException(e);
        } catch (Exception e) {
            sender.sendMessage("There was an error. Check your console for more info.");
            LoggerUtil.logSevereException(e);
        }
    }

    public abstract void onExecute() throws ConfigurateException;

    public abstract String getMigrationType();

    public abstract int getTotalAmount();

}
