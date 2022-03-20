package net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate;

import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
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
public class DeckMigratorBukkitRunnable extends BukkitRunnable {
    private final CommandSender sender;
    private final TradingCards plugin;

    public DeckMigratorBukkitRunnable(final TradingCards plugin, final CommandSender sender) {
        this.plugin = plugin;
        this.sender = sender;
    }

    @Override
    public void run() {
        long startTime = System.nanoTime();
        try {
            YamlStorage yamlStorage = new YamlStorage(plugin);
            yamlStorage.init(plugin);
            Map<UUID, List<Deck>> yamlDecks = yamlStorage.getAllDecks();
            sender.sendMessage("Found " + yamlDecks.size() + " players.");

            int totalDecks = yamlDecks.values().stream()
                    .mapToInt(Collection::size)
                    .sum();
            sender.sendMessage("Total " + totalDecks + " decks.");

            for (Map.Entry<UUID, List<Deck>> entry : yamlDecks.entrySet()) {
                final UUID playerUuid = entry.getKey();
                sender.sendMessage(ChatUtil.color("&2Started conversion for " + playerUuid));
                for (Deck deck : entry.getValue()) {
                    plugin.getStorage().saveDeck(playerUuid, deck.getNumber(), deck);
                }
                sender.sendMessage(ChatUtil.color("&2Finished conversion for " + playerUuid + ", converted " + entry.getValue().size() + " decks."));
            }
            long endTime = System.nanoTime();
            sender.sendMessage(ChatUtil.color("&2Finished conversion of " + totalDecks + " decks."));
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
}
