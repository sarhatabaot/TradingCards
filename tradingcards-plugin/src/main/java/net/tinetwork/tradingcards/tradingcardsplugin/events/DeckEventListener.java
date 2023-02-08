package net.tinetwork.tradingcards.tradingcardsplugin.events;

import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.events.DeckCloseEvent;
import net.tinetwork.tradingcards.api.events.DeckItemInteractEvent;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.listeners.SimpleListener;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingDeckManager;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DeckEventListener extends SimpleListener {
    private final TradingDeckManager deckManager;

    public DeckEventListener(TradingCards plugin) {
        super(plugin);
        this.deckManager = plugin.getDeckManager();
    }

    //Limits the transfer of more than 1 itemstack to a deck.
    @EventHandler
    public void onAddItem(final @NotNull InventoryMoveItemEvent event) {
        final Inventory destination = event.getDestination();
        if(!(event.getInitiator().getHolder() instanceof Player player)) {
            debug(InternalDebug.DeckEventListener.NOT_A_PLAYER);
            return;
        }

        if (destination.getType() != InventoryType.CHEST) {
            return;
        }
        final UUID uuid = player.getUniqueId();
        if (!deckManager.containsViewer(player.getUniqueId())) {
            debug(InternalDebug.DeckEventListener.NOT_OUR_GUI.formatted(uuid));
            return;
        }

        if(!destination.containsAtLeast(event.getItem(), 1)) {
            debug(InternalDebug.DeckEventListener.NO_ITEMS_OF_TYPE);
            return;
        }


        NBTItem nbtItem = new NBTItem(event.getItem());
        if(!NbtUtils.Card.isCard(nbtItem)) {
            //not a card, ignoring
            return;
        }

        if(containsAtLeast(destination,nbtItem)) {
            event.setCancelled(true);
            ChatUtil.sendPrefixedMessage(player, InternalMessages.CANNOT_HAVE_MORE_THAN_A_STACK);
        }

    }

    private boolean containsAtLeast(final Inventory inventory, final NBTItem nbtItem) {
        int amountOfItem = getAmountOfItem(inventory,nbtItem);
        return amountOfItem >= 64;
    }

    private int getAmountOfItem(final @NotNull Inventory inventory, final NBTItem nbtItem) {
        int amount = 0;
        for(ItemStack itemStack: inventory.getContents()) {
            NBTItem currentItem = new NBTItem(itemStack);
            if(NbtUtils.isCardSimilar(currentItem,nbtItem)) {
                amount += currentItem.getItem().getAmount();
            }
        }
        return amount;
    }

    @EventHandler
    public void onItemDeck(final @NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        final Block clickedBlock = event.getClickedBlock();
        if(clickedBlock != null && isContainer(clickedBlock.getType())) {
            return;
        }
        
        final EquipmentSlot e = event.getHand();
        if (e == null || !e.equals(EquipmentSlot.HAND)) {
            return;
        }


        final Player player = event.getPlayer();
        final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (!plugin.getDeckManager().isDeck(itemInMainHand)) {
            return;
        }


        if (player.getGameMode() == GameMode.CREATIVE) {
            ChatUtil.sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().deckCreativeError()));
            return;
        }

        int deckNumber = deckManager.getDeckNumber(player.getInventory().getItemInMainHand());
        Bukkit.getPluginManager().callEvent(new DeckItemInteractEvent(event.getPlayer(), event.getAction(), event.getItem(), event.getClickedBlock(), event.getBlockFace(), deckNumber));
    }
    private boolean isContainer(Material material) {
        return material == Material.CHEST ||
            material == Material.CHEST_MINECART ||
            material == Material.DISPENSER ||
            material == Material.HOPPER ||
            material == Material.HOPPER_MINECART ||
            material == Material.ENDER_CHEST ||
            material == Material.TRAPPED_CHEST ||
            material == Material.BREWING_STAND ||
            material == Material.FURNACE ||
            material == Material.FURNACE_MINECART ||
            material == Material.SHULKER_BOX ||
            material == Material.DROPPER;
    }

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        if (event.getInventory().getType() != InventoryType.CHEST) {
            return;
        }
        final UUID uuid = event.getPlayer().getUniqueId();
        if (!deckManager.containsViewer(event.getPlayer().getUniqueId())) {
            debug(InternalDebug.DeckEventListener.NOT_OUR_GUI.formatted(uuid));
            return;
        }

        if (!(event.getPlayer() instanceof final Player player)) {
            debug(InternalDebug.DeckEventListener.NOT_A_PLAYER);
            return;
        }

        int deckNum = deckManager.getViewerDeckNum(player.getUniqueId());
        debug(InternalDebug.DeckEventListener.DECK_PLAYER.formatted(deckNum,player.getName(),player.getUniqueId()));

        Bukkit.getPluginManager().callEvent(new DeckCloseEvent(event.getView(),deckNum));
    }
}
