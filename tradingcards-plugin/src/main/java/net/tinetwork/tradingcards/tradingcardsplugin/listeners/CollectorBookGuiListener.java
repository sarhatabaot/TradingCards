package net.tinetwork.tradingcards.tradingcardsplugin.listeners;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.collector.gui.CollectorBookGuiManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class CollectorBookGuiListener extends SimpleListener {
    private final CollectorBookGuiManager collectorBookGuiManager;

    public CollectorBookGuiListener(final @NotNull TradingCards plugin, final @NotNull CollectorBookGuiManager collectorBookGuiManager) {
        super(plugin);
        this.collectorBookGuiManager = collectorBookGuiManager;
    }

    @EventHandler
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        collectorBookGuiManager.handleClick(event);
    }

    @EventHandler
    public void onInventoryClose(final @NotNull InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        final var playerUuid = player.getUniqueId();
        if (!collectorBookGuiManager.isViewer(playerUuid)) {
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            final String nextTitle = player.getOpenInventory() == null ? null : player.getOpenInventory().getTitle();
            if (!collectorBookGuiManager.isCollectorInventoryTitle(nextTitle)) {
                collectorBookGuiManager.removeViewer(playerUuid);
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(final @NotNull PlayerQuitEvent event) {
        collectorBookGuiManager.removeViewer(event.getPlayer().getUniqueId());
    }
}
