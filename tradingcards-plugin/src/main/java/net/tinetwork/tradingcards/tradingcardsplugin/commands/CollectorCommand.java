package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Values;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.collector.CollectorBookManager;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@CommandAlias("cards")
public class CollectorCommand extends BaseCommand {
    private final TradingCards plugin;
    private final CollectorBookManager collectorBookManager;

    public CollectorCommand(final @NotNull TradingCards plugin) {
        this.plugin = plugin;
        this.collectorBookManager = plugin.getCollectorBookManager();
    }

    @Subcommand("collector")
    @CommandPermission(Permissions.User.List.COLLECTOR)
    @Description("Open your Collector Book GUI.")
    public void onCollectorOpen(final @NotNull Player player) {
        openCollectorGui(player);
    }

    @Subcommand("collector open")
    @CommandPermission(Permissions.User.List.COLLECTOR)
    @Description("Open your Collector Book GUI.")
    public void onCollectorOpenSubcommand(final @NotNull Player player) {
        openCollectorGui(player);
    }

    @Subcommand("collector migrate")
    @CommandPermission(Permissions.Admin.ADMIN_MIGRATE)
    @Description("Migrates collector data for a player or all players with stored decks.")
    public void onCollectorMigrate(
            final @NotNull CommandSender sender,
            final @NotNull String target,
            final @Optional @Values("@bool") String forceArg,
            final @Optional @Values("@bool") String dryRunArg
    ) {
        final boolean force = Boolean.parseBoolean(forceArg);
        final boolean dryRun = Boolean.parseBoolean(dryRunArg);

        if ("all".equalsIgnoreCase(target)) {
            migrateAll(sender, force, dryRun);
            return;
        }

        final UUID targetUuid = resolvePlayerUuid(target);
        if (targetUuid == null) {
            ChatUtil.sendPrefixedMessage(sender, "Could not resolve player '" + target + "'. Use online name, known offline name, UUID, or 'all'.");
            return;
        }

        final CollectorBookManager.CollectorMigrationSummary summary = collectorBookManager.migratePlayer(targetUuid, force, dryRun);
        sendSummary(sender, summary);
    }

    private void migrateAll(final @NotNull CommandSender sender, final boolean force, final boolean dryRun) {
        final Set<UUID> playerUuids = plugin.getStorage().getPlayersWithDecks();
        if (playerUuids.isEmpty()) {
            ChatUtil.sendPrefixedMessage(sender, "No players with deck data were found.");
            return;
        }

        int withWork = 0;
        int migratedBooks = 0;
        int normalizedDecks = 0;
        int skippedByGuard = 0;

        for (UUID playerUuid : playerUuids) {
            final CollectorBookManager.CollectorMigrationSummary summary = collectorBookManager.migratePlayer(playerUuid, force, dryRun);
            if (summary.hasWork()) {
                withWork++;
            }
            if (summary.collectorBookMigrated()) {
                migratedBooks++;
            }
            normalizedDecks += summary.legacyDecksNeedingNormalization();
            if (summary.skippedByGuard()) {
                skippedByGuard++;
            }
        }

        ChatUtil.sendPrefixedMessage(sender,
                "Collector migrate all complete: players=%d, withWork=%d, migratedBooks=%d, decksNeedingNormalization=%d, skippedByGuard=%d, dryRun=%s, force=%s"
                        .formatted(playerUuids.size(), withWork, migratedBooks, normalizedDecks, skippedByGuard, dryRun, force));
    }

    private void sendSummary(final @NotNull CommandSender sender, final @NotNull CollectorBookManager.CollectorMigrationSummary summary) {
        ChatUtil.sendPrefixedMessage(sender,
                "Collector migrate %s: work=%s, migratedBook=%s, legacyDecks=%d, legacyOwnershipEntries=%d, decksNeedingNormalization=%d, skippedByGuard=%s, dryRun=%s, force=%s"
                        .formatted(
                                summary.playerUuid(),
                                summary.hasWork(),
                                summary.collectorBookMigrated(),
                                summary.legacyDeckCount(),
                                summary.legacyOwnershipEntries(),
                                summary.legacyDecksNeedingNormalization(),
                                summary.skippedByGuard(),
                                summary.dryRun(),
                                summary.force()
                        ));
    }

    private @Nullable UUID resolvePlayerUuid(final @NotNull String target) {
        final Player onlinePlayer = Bukkit.getPlayerExact(target);
        if (onlinePlayer != null) {
            return onlinePlayer.getUniqueId();
        }

        try {
            return UUID.fromString(target);
        } catch (IllegalArgumentException ignored) {
            // not a UUID
        }

        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(target);
        if (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) {
            return offlinePlayer.getUniqueId();
        }

        return null;
    }

    private void openCollectorGui(final @NotNull Player player) {
        if (!plugin.getGeneralConfig().collectorBookEnabled()) {
            ChatUtil.sendPrefixedMessage(player, "Collector Book is disabled. Enable 'collector-book-enabled' in settings/general.yml.");
            return;
        }

        plugin.getCollectorBookGuiManager().openMainMenu(player);
    }
}
