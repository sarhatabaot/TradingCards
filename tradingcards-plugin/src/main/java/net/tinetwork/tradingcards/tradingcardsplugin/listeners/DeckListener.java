package net.tinetwork.tradingcards.tradingcardsplugin.listeners;

import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.events.DeckCloseEvent;
import net.tinetwork.tradingcards.api.events.DeckItemInteractEvent;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.config.DeckConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingDeckManager;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DeckListener extends SimpleListener {
    private final TradingDeckManager deckManager;
    private final DeckConfig deckConfig;

    public DeckListener(final TradingCards plugin) {
        super(plugin);
        this.deckManager = plugin.getDeckManager();
        this.deckConfig = plugin.getDeckStorage();
    }

    @EventHandler
    public void onItemDeck(final @NotNull DeckItemInteractEvent event) {
        final Player player = event.getPlayer();
        final int deckNumber = event.getDeckNumber();
        deckManager.openDeck(player, deckNumber);
    }


    @EventHandler
    public void onDeckClose(@NotNull DeckCloseEvent e) {
        final Player player = (Player) e.getPlayer();
        final int deckNum = e.getDeckNumber();

        List<StorageEntry> serializedEntries = new ArrayList<>();
        final List<ItemStack> inventoryContents = Arrays.stream(e.getInventory().getContents())
                .filter(Objects::nonNull)
                .toList();

        for(ItemStack item: inventoryContents) {
            if (!CardUtil.isCard(item) && plugin.getGeneralConfig().dropDeckItems()) {
                CardUtil.dropItem(player, item);
                continue;
            }

            StorageEntry entry = formatEntryString(item);
            serializedEntries.add(entry);
            debug("Added " + entry + " to deck file.");
        }

        deckConfig.saveEntries(player.getUniqueId(), deckNum, serializedEntries);
        deckConfig.reloadConfig();
        deckManager.removeDeckViewer(e.getPlayer().getUniqueId());
        debug("Deck closed");
    }

    private @NotNull StorageEntry formatEntryString(final ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        final String cardId = nbtItem.getString(NbtUtils.NBT_CARD_NAME);
        final String rarity = nbtItem.getString(NbtUtils.NBT_RARITY);
        final boolean shiny = nbtItem.getBoolean(NbtUtils.NBT_CARD_SHINY);
        return new StorageEntry(rarity, cardId, itemStack.getAmount(), shiny);
    }
}
