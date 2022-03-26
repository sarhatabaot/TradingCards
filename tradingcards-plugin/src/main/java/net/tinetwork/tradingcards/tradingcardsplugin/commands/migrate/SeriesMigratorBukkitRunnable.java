package net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate;

import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.ConfigurateException;

/**
 * @author sarhatabaot
 */
public class SeriesMigratorBukkitRunnable extends MigratorBukkitRunnable{
    public SeriesMigratorBukkitRunnable(final TradingCards plugin, final CommandSender sender, final Storage<TradingCard> source) {
        super(plugin, sender, source);
    }

    @Override
    public void onExecute() throws ConfigurateException {
        for(Series series: source.getAllSeries()) {
            Util.logAndMessage(sender,"Started conversion for "+series.getId());
            plugin.getStorage().createSeries(series.getId());
            plugin.getStorage().editSeriesMode(series.getId(),series.getMode());
            plugin.getStorage().editSeriesDisplayName(series.getId(),series.getDisplayName());
            plugin.getStorage().createColorSeries(series.getId());
            plugin.getStorage().editColorSeries(series.getId(),series.getColorSeries());
            plugin.getStorage().editSeriesColors(series.getId(),series.getColorSeries());
        }
    }


    @Override
    public String getMigrationType() {
        return "series";
    }

    @Override
    public int getTotalAmount() {
        return source.getAllSeries().size();
    }
}
