package net.tinetwork.tradingcards.tradingcardsplugin.listeners;

import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.events.DeckCloseEvent;
import net.tinetwork.tradingcards.api.model.deck.DeckEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.config.DeckConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingDeckManager;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DeckListener extends SimpleListener {
    private final TradingDeckManager deckManager;
    private final DeckConfig deckConfig;

    public DeckListener(final TradingCards plugin) {
        super(plugin);
        this.deckManager = plugin.getDeckManager();
        this.deckConfig = plugin.getDeckConfig();
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

        int num = deckManager.getDeckNumber(player.getInventory().getItemInMainHand());
        deckManager.openDeck(player, num);
    }


    @EventHandler
    public void onDeckClose(@NotNull DeckCloseEvent e) {
        final Player player = (Player) e.getPlayer();
        final int deckNum = e.getDeckNumber();

        List<DeckEntry> serializedEntries = new ArrayList<>();
        final List<ItemStack> inventoryContents = Arrays.stream(e.getInventory().getContents())
                .filter(Objects::nonNull)
                .toList();

        for(ItemStack item: inventoryContents) {
            if (!CardUtil.isCard(item) && plugin.getGeneralConfig().dropDeckItems()) {
                CardUtil.dropItem(player, item);
                continue;
            }

            DeckEntry entry = formatEntryString(item);
            serializedEntries.add(entry);
            debug("Added " + entry + " to deck file.");
        }

        deckConfig.saveEntries(player.getUniqueId(), deckNum, serializedEntries);
        deckConfig.reloadConfig();
        deckManager.closeDeckViewer(e.getPlayer().getUniqueId());
        debug("Deck closed");
    }

    private @NotNull DeckEntry formatEntryString(final ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        final String cardId = nbtItem.getString("name");
        final String rarity = nbtItem.getString("rarity");
        final boolean shiny = nbtItem.getBoolean("isShiny");
        return new DeckEntry(rarity, cardId, itemStack.getAmount(), shiny);
    }
}
