package net.tinetwork.tradingcards.tradingcardsplugin.customitem.itemsadder;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.customitem.CustomItemIntegration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ItemsAdderCustomItemIntegration implements CustomItemIntegration, Listener {
    private final TradingCards plugin;

    public ItemsAdderCustomItemIntegration(final @NotNull TradingCards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isAvailable() {
        return plugin.getServer().getPluginManager().isPluginEnabled("ItemsAdder");
    }

    @Override
    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onItemsAdderLoadData(final @NotNull ItemsAdderLoadDataEvent event) {
        plugin.getLogger().info(() -> "Reloading TradingCards after ItemsAdder data load: " + event.getCause());
        plugin.reloadPlugin();
    }
}
