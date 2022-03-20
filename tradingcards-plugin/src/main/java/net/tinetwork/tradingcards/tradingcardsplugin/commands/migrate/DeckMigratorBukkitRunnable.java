package net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate;

import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local.YamlStorage;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.spongepowered.configurate.ConfigurateException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author sarhatabaot
 */
public class DeckMigratorBukkitRunnable extends MigratorBukkitRunnable {
    public DeckMigratorBukkitRunnable(final TradingCards plugin, final CommandSender sender, final Storage<TradingCard> source) {
        super(plugin, sender, source);
    }

    @Override
    public void onExecute() throws ConfigurateException {
        //Assume that the source is yaml, change later
        YamlStorage yamlStorage = (YamlStorage) source;
        Map<UUID, List<Deck>> yamlDecks = yamlStorage.getAllDecks();
        Util.logAndMessage(sender,"Found " + yamlDecks.size() + " players.");

        int totalDecks = yamlDecks.values().stream()
                .mapToInt(Collection::size)
                .sum();

        Util.logAndMessage(sender, "Total " + totalDecks + " decks.");

        for (Map.Entry<UUID, List<Deck>> entry : yamlDecks.entrySet()) {
            final UUID playerUuid = entry.getKey();
            Util.logAndMessage(sender,"&2Started conversion for " + playerUuid);
            for (Deck deck : entry.getValue()) {
                plugin.getStorage().saveDeck(playerUuid, deck.getNumber(), deck);
            }
            Util.logAndMessage(sender,"&2Finished conversion for " + playerUuid + ", converted " + entry.getValue().size() + " decks.");
        }
        Util.logAndMessage(sender, "&2Finished conversion of " + totalDecks + " decks.");
    }
}
