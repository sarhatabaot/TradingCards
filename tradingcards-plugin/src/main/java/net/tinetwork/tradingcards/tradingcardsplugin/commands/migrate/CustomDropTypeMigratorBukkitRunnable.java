package net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate;

import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
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
    public String getMigrationType() {
        return "custom-types";
    }

    @Override
    public int getTotalAmount() {
        return source.getDropTypes().size();
    }

    @Override
    public void onExecute() throws ConfigurateException {
        for(DropType dropType: source.getDropTypes()) {
            plugin.getStorage().createCustomType(dropType.getId(),dropType.getMobGroup());
            plugin.getStorage().editCustomTypeDisplayName(dropType.getId(),dropType.getDisplayName());
        }
    }
}
