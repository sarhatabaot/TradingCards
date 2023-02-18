package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate.UpgradeMigratorBukkitRunnable;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.Permissions;
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
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;

/**
 * @author sarhatabaot
 */
@CommandAlias("cards")
public class MigrateCommand extends BaseCommand {
    private final TradingCards plugin;
    private boolean ranDataMigration = false;
    public MigrateCommand(final TradingCards plugin) {
        this.plugin = plugin;
    }

    @Subcommand("migrate")
    @CommandPermission(Permissions.Admin.ADMIN_MIGRATE)
    @Description("Migrates from yaml to another storage type.")
    public class MigrateSubCommand extends BaseCommand {
        @Default
        @Subcommand("all")
        public void onMigrateInfo(final CommandSender sender) {
            if (plugin.getStorage().getType() == StorageType.YAML) {
                ChatUtil.sendMessage(sender, InternalMessages.Migrate.YAML_TO_YAML);
                ChatUtil.sendMessage(sender, InternalMessages.Migrate.CHANGE_STORAGE_TYPE);
                return;
            }

            ChatUtil.sendMessage(sender,InternalMessages.Migrate.WARNING);
            ChatUtil.sendMessage(sender,InternalMessages.Migrate.BACKUP_HINT1);
            ChatUtil.sendMessage(sender,InternalMessages.Migrate.BACKUP_HINT2);
            ChatUtil.sendMessage(sender, InternalMessages.Migrate.CONFIRM_HINT.formatted(plugin.getStorage().getType().name()));
            ChatUtil.sendMessage(sender,InternalMessages.Migrate.CONFIRM_CMD);
        }


        @Subcommand("data confirm")
        @Description("Converts all data files other than decks.")
        public void onDataMigrateConfirm(final @NotNull CommandSender sender) {
            ChatUtil.sendMessage(sender,InternalMessages.MigrateConfirm.START_MIGRATION.formatted("data",plugin.getStorage().getType().name()));
            ChatUtil.sendMessage(sender,InternalMessages.MigrateConfirm.WARNING);
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
            new UpgradeMigratorBukkitRunnable(plugin,sender,yamlStorage).runTask(plugin);
            new PackMigratorBukkitRunnable(plugin,sender,yamlStorage).runTask(plugin);
            new CustomDropTypeMigratorBukkitRunnable(plugin,sender,yamlStorage).runTask(plugin);
            new CardMigratorBukkitRunnable(plugin,sender,yamlStorage).runTask(plugin);
            ranDataMigration = true;

            ChatUtil.sendMessage(sender,InternalMessages.MigrateConfirm.COMPLETE_MIGRATION.formatted(plugin.getStorage().getType().name()));
            ChatUtil.sendMessage(sender,InternalMessages.MigrateConfirm.RESTART_HINT);
        }

        @Subcommand("deck confirm")
        @Description("Converts decks.")
        public void onDeckMigrateConfirm(final @NotNull CommandSender sender) {
            ChatUtil.sendMessage(sender,InternalMessages.MigrateConfirm.START_MIGRATION.formatted("decks",plugin.getStorage().getType().name()));
            ChatUtil.sendMessage(sender,InternalMessages.MigrateConfirm.WARNING);
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


    public boolean isRanDataMigration() {
        return ranDataMigration;
    }
}
