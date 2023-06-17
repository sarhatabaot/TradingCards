package net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.LoggerUtil;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.ConfigurateException;

/**
 * @author sarhatabaot
 */
public class UpgradeMigratorBukkitRunnable extends MigratorBukkitRunnable{
    public UpgradeMigratorBukkitRunnable(final TradingCards plugin, final CommandSender sender, final Storage<TradingCard> source) {
        super(plugin, sender, source);
    }

    @Override
    public void onExecute() throws ConfigurateException {
        source.getUpgrades().forEach(upgrade -> {
            LoggerUtil.logAndMessage(sender, InternalMessages.STARTED_CONVERSION_FOR.formatted(upgrade.id()));
            plugin.getStorage().createUpgrade(upgrade.id(), upgrade.required(),upgrade.result());
        });
    }

    @Override
    public String getMigrationType() {
        return "upgrades";
    }

    @Override
    public int getTotalAmount() {
        return source.getUpgrades().size();
    }
}
