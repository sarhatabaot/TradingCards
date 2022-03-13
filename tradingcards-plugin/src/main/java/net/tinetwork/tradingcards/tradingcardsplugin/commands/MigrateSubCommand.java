package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.tradingcardsplugin.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.StorageType;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local.YamlStorage;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author sarhatabaot
 */
@Subcommand("migrate")
@CommandPermission(Permissions.ADMIN_MIGRATE)
@Description("Migrates from yaml to another storage type.")
public class MigrateSubCommand extends BaseCommand {
    private final TradingCards plugin;

    public MigrateSubCommand(final TradingCards plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onMigrateInfo(final CommandSender sender) {
        if (plugin.getStorage().getType() == StorageType.YAML) {
            sender.sendMessage(ChatUtil.color("&4Cannot convert from YAML to YAML."));
            sender.sendMessage(ChatUtil.color("&4Please change your storage type to MYSQL or MARIADB & restart your server."));
            return;
        }

        sender.sendMessage(ChatUtil.color("&cAre you sure you want to migrate? This action is irreversible."));
        sender.sendMessage(ChatUtil.color("&cMake sure you have made a backup of your decks.yml before continuing."));
        sender.sendMessage(ChatUtil.color("&cIf you want to convert from YAML to " + plugin.getStorage().getType().name()));
        sender.sendMessage(ChatUtil.color("&cPlease type /cards migrate confirm"));
    }


    @Subcommand("confirm")
    public void onMigrateConfirm(final @NotNull CommandSender sender) {
        sender.sendMessage(ChatUtil.color("&2Started migration from YAML to " + plugin.getStorage().getType().name()));
        sender.sendMessage(ChatUtil.color("&2This may take a while..."));
        new MigratorBukkitRunnable(sender).runTask(plugin);
    }

    public class MigratorBukkitRunnable extends BukkitRunnable {
        private final CommandSender sender;

        public MigratorBukkitRunnable(final CommandSender sender) {
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
}
