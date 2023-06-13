package net.tinetwork.tradingcards.tradingcardsplugin.listeners;

import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.events.DeckCloseEvent;
import net.tinetwork.tradingcards.api.events.DeckItemInteractEvent;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.impl.TradingDeckManager;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DeckListener extends SimpleListener {
    private final TradingDeckManager deckManager;
    private final Storage<TradingCard> deckStorage;

    public DeckListener(final TradingCards plugin) {
        super(plugin);
        this.deckManager = plugin.getDeckManager();
        this.deckStorage = plugin.getStorage();
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
            if(!updateSimilarEntry(serializedEntries,entry))
                serializedEntries.add(entry);
            debug(entry.toString());
            debug(InternalDebug.DeckListener.ADDED_ENTRY.formatted(entry));
        }

        final Deck deck = new Deck(player.getUniqueId(),deckNum,serializedEntries);
        deckStorage.saveDeck(player.getUniqueId(), deckNum, deck);
        deckManager.removeDeckViewer(e.getPlayer().getUniqueId());
        debug(InternalDebug.DeckListener.DECK_CLOSED);
    }

    private boolean updateSimilarEntry(final @NotNull List<StorageEntry> entries, final StorageEntry entryToCompare) {
        boolean wasChanged = false;
        for(StorageEntry storageEntry: entries) {
            if(storageEntry.isSimilar(entryToCompare)) {
                storageEntry.setAmount(storageEntry.getAmount() + entryToCompare.getAmount());
                wasChanged = true;
            }
        }

        return wasChanged;
    }

    private @NotNull StorageEntry formatEntryString(final ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        final String cardId = NbtUtils.Card.getCardId(nbtItem);
        final String rarity = NbtUtils.Card.getRarityId(nbtItem);
        final boolean shiny = NbtUtils.Card.isShiny(nbtItem);
        final String seriesId = NbtUtils.Card.getSeriesId(nbtItem);
        return new StorageEntry(rarity, cardId, itemStack.getAmount(), shiny,seriesId);
    }
}
