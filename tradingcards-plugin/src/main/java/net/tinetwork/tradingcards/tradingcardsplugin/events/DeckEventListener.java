package net.tinetwork.tradingcards.tradingcardsplugin.events;

import net.tinetwork.tradingcards.api.events.DeckCloseEvent;
import net.tinetwork.tradingcards.api.events.DeckItemInteractEvent;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.listeners.SimpleListener;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingDeckManager;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DeckEventListener extends SimpleListener {
    private final TradingDeckManager deckManager;

    public DeckEventListener(TradingCards plugin) {
        super(plugin);
        this.deckManager = plugin.getDeckManager();
    }

    @EventHandler
    public void onItemDeck(final @NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        EquipmentSlot e = event.getHand();
        if (e == null || !e.equals(EquipmentSlot.HAND)) {
            return;
        }


        Player player = event.getPlayer();
        final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (!plugin.getDeckManager().isDeck(itemInMainHand))
            return;


        if (player.getGameMode() == GameMode.CREATIVE) {
            ChatUtil.sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().deckCreativeError()));
            return;
        }

        int deckNumber = deckManager.getDeckNumber(player.getInventory().getItemInMainHand());
        Bukkit.getPluginManager().callEvent(new DeckItemInteractEvent(event.getPlayer(), event.getAction(), event.getItem(), event.getClickedBlock(), event.getBlockFace(), deckNumber));
    }

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        if (event.getInventory().getType() != InventoryType.CHEST) {
            plugin.trace("Not a chest=" + event.getInventory().getType());
            return;
        }
        final UUID uuid = event.getPlayer().getUniqueId();
        if (!deckManager.containsViewer(event.getPlayer().getUniqueId())) {
            plugin.debug(getClass(), "Not our gui, ignoring. UUID: " + uuid);
            return;
        }

        if (!(event.getPlayer() instanceof final Player player)) {
            plugin.debug(getClass(), "Not a player entity, ignoring.");
            return;
        }

        int deckNum = deckManager.getViewerDeckNum(player.getUniqueId());
        debug("deck: " + deckNum + ",player: " + player.getName());

        Bukkit.getPluginManager().callEvent(new DeckCloseEvent(event.getView(), event.getReason(), deckNum));
    }
}
