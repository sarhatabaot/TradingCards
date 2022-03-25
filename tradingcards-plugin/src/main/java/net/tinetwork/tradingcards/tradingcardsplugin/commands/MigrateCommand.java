package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.tinetwork.tradingcards.tradingcardsplugin.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate.CardMigratorBukkitRunnable;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate.CustomDropTypeMigratorBukkitRunnable;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate.DeckMigratorBukkitRunnable;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate.PackMigratorBukkitRunnable;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate.RarityMigratorBukkitRunnable;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate.SeriesMigratorBukkitRunnable;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.StorageType;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local.YamlStorage;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;

/**
 * @author sarhatabaot
 */
@CommandAlias("cards")
public class MigrateCommand extends BaseCommand {
    private final TradingCards plugin;

    public MigrateCommand(final TradingCards plugin) {
        this.plugin = plugin;
    }

    @Subcommand("migrate")
    @CommandPermission(Permissions.ADMIN_MIGRATE)
    @Description("Migrates from yaml to another storage type.")
    public class MigrateSubCommand extends BaseCommand {
        @Default
        @Subcommand("all")
        public void onMigrateInfo(final CommandSender sender) {
            if (plugin.getStorage().getType() == StorageType.YAML) {
                sender.sendMessage(ChatUtil.color("&4Cannot convert from YAML to YAML."));
                sender.sendMessage(ChatUtil.color("&4Please change your storage type to MYSQL or MARIADB & restart your server."));
                return;
            }

            sender.sendMessage(ChatUtil.color("&cAre you sure you want to migrate? This action is irreversible."));
            sender.sendMessage(ChatUtil.color("&cMake sure you have made a backup of your decks.yml before continuing."));
            sender.sendMessage(ChatUtil.color("&cIf you want to convert from YAML to " + plugin.getStorage().getType().name()));
            sender.sendMessage(ChatUtil.color("&cPlease type /cards migrate <deck|data> confirm"));
        }


        @Subcommand("data confirm")
        @Description("Converts all data files other than decks.")
        public void onDataMigrateConfirm(final @NotNull CommandSender sender) {
            sender.sendMessage(ChatUtil.color("&2Started migration for data from YAML to " + plugin.getStorage().getType().name()));
            sender.sendMessage(ChatUtil.color("&2This may take a while..."));
            YamlStorage yamlStorage;
            try {
                yamlStorage = new YamlStorage(plugin);
                yamlStorage.init(plugin);
            } catch (ConfigurateException e){
                Util.logSevereException(e);
                return;
            }
            new RarityMigratorBukkitRunnable(plugin,sender,yamlStorage).runTask(plugin);
            new SeriesMigratorBukkitRunnable(plugin,sender,yamlStorage).runTask(plugin);
            new PackMigratorBukkitRunnable(plugin,sender,yamlStorage).runTask(plugin);
            new CustomDropTypeMigratorBukkitRunnable(plugin,sender,yamlStorage).runTask(plugin);
            new CardMigratorBukkitRunnable(plugin,sender,yamlStorage).runTask(plugin);
        }

        @Subcommand("deck confirm")
        @Description("Converts decks.")
        public void onDeckMigrateConfirm(final @NotNull CommandSender sender) {
            sender.sendMessage(ChatUtil.color("&2Started migration for decks from YAML to " + plugin.getStorage().getType().name()));
            sender.sendMessage(ChatUtil.color("&2This may take a while..."));
            YamlStorage yamlStorage;
            try {
                yamlStorage = new YamlStorage(plugin);
                yamlStorage.init(plugin);
            } catch (ConfigurateException e){
                Util.logSevereException(e);
                return;
            }
            new DeckMigratorBukkitRunnable(plugin,sender, yamlStorage).runTask(plugin);
        }

    }

}
