package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ExcludeFileFilter;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.tinetwork.tradingcards.api.addons.TradingCardsAddon;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * @author sarhatabaot
 */

@CommandAlias("cards")
public class DebugCommands extends BaseCommand {
    private final TradingCards plugin;

    public DebugCommands(final TradingCards plugin) {
        this.plugin = plugin;
    }

    @Subcommand("debug")
    @CommandPermission("cards.admin.debug")
    public class DebugSubCommands extends BaseCommand {

        public class ZipBukkitRunnable extends BukkitRunnable {
            private final CommandSender sender;

            public ZipBukkitRunnable(final CommandSender sender) {
                this.sender = sender;
            }

            @Override
            public void run() {
                final String pluginFolder = plugin.getDataFolder().getPath();
                final String cardsFolder = pluginFolder + File.separator + "cards";
                final String dataFolder = pluginFolder + File.separator + "data";
                final String listsFolder = pluginFolder + File.separator + "lists";
                final File settingsFolder = new File(pluginFolder + File.separator + "settings");
                ExcludeFileFilter excludeFileFilter = file -> file.getName().contains("storage.yml");
                ZipParameters zipParameters = new ZipParameters();
                zipParameters.setExcludeFileFilter(excludeFileFilter);
                try (ZipFile zipFile = new ZipFile(pluginFolder + File.separator + "debug.zip")) {
                    zipFile.addFolder(new File(cardsFolder));
                    zipFile.addFolder(new File(dataFolder));
                    zipFile.addFolder(new File(listsFolder));
                    zipFile.addFolder(settingsFolder, zipParameters);
                    if (zipFile.getProgressMonitor().getResult().equals(ProgressMonitor.Result.SUCCESS)) {
                        sender.sendMessage("Added all settings files to debug.zip.");
                    }
                } catch (IOException e) {
                    Util.logWarningException(e);
                }

            }
        }

        @Subcommand("zip")
        @CommandPermission(Permissions.ADMIN_DEBUG_ZIP)
        @Description("Creates a zip of all settings.")
        public void onZip(final @NotNull CommandSender sender) {
            sender.sendMessage("Backing the settings folder to debug.zip");
            sender.sendMessage("This does not backup storage.yml.");

            new ZipBukkitRunnable(sender).runTask(plugin);
        }

        @Subcommand("showcache all")
        @CommandPermission(Permissions.ADMIN_DEBUG_SHOW_CACHE)
        @Description("Shows the card cache")
        public void showCacheAll(final @NotNull CommandSender sender) {
            sender.sendMessage(StringUtils.join(plugin.getCardManager().getCards().keySet(), ","));
        }

        @Subcommand("showcache active")
        @CommandPermission(Permissions.ADMIN_DEBUG_SHOW_CACHE)
        @Description("Shows the card cache")
        public void showCacheActive(final @NotNull CommandSender sender) {
            sender.sendMessage(StringUtils.join(plugin.getCardManager().getActiveCards(), ","));
        }

        @Subcommand("modules")
        @CommandPermission(Permissions.ADMIN_DEBUG_MODULES)
        @Description("Shows all enabled hooks and addons.")
        public void onModules(final CommandSender sender) {
            final StringBuilder builder = new StringBuilder("Enabled Modules/Addons:");
            builder.append("\n");
            builder.append(ChatColor.GOLD).append(ChatColor.BOLD).append("Modules:");
            for (String depend : plugin.getDescription().getSoftDepend()) {
                if (Bukkit.getPluginManager().getPlugin(depend) == null)
                    builder.append(ChatColor.GRAY);
                else {
                    builder.append(ChatColor.GREEN);
                }
                builder.append(depend).append(" ");
            }
            builder.append(ChatColor.GOLD).append(ChatColor.BOLD).append("Addons:");
            for (Plugin bukkitPlugin : Bukkit.getPluginManager().getPlugins()) {
                if (plugin instanceof TradingCardsAddon)
                    builder.append(ChatColor.GREEN).append(bukkitPlugin.getName()).append(" ");
            }

            sender.sendMessage(builder.toString());
        }

        @Subcommand("packs")
        @CommandPermission(Permissions.ADMIN_DEBUG_PACKS)
        @Description("Show all available packs.")
        public void onPack(final CommandSender sender) {
            sender.sendMessage(StringUtils.join(plugin.getPackManager().getPacks(), ","));
        }

        @Subcommand("rarities")
        @CommandPermission(Permissions.ADMIN_DEBUG_RARITIES)
        @Description("Shows available rarities.")
        public void onRarities(final CommandSender sender) {
            StringBuilder sb = new StringBuilder();
            for (Rarity rarity : plugin.getRarityManager().getRarities()) {
                sb.append(rarity.getId()).append(", ");
            }
            sender.sendMessage(sb.toString());
        }

        @Subcommand("exists")
        @CommandPermission(Permissions.ADMIN_DEBUG_EXISTS)
        @Description("Shows if a card exists or not.")
        public void onExists(final CommandSender sender, final String card, final String rarity) {
            if (plugin.getCardManager().getCards().containsKey(rarity + "." + card)) {
                sender.sendMessage(String.format("Card %s.%s exists", rarity, card));
                return;
            }
            sender.sendMessage(String.format("Card %s.%s does not exist", rarity, card));
        }

        @Subcommand("rarities-series")
        @CommandCompletion("@rarities @series")
        @CommandPermission(Permissions.ADMIN_DEBUG_RARITIES_SERIES)
        public void onRaritiesSeriesCards(final CommandSender sender, final String rarityId, final String seriesId) {
            sender.sendMessage(StringUtils.join(plugin.getStorage().getCardsInRarityAndSeries(rarityId, seriesId).stream().map(TradingCard::getCardId).toList(), ", "));
        }
    }
}
