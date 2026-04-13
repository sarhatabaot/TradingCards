package net.tinetwork.tradingcards.tradingcardsplugin.listeners;


import net.tinetwork.tradingcards.api.events.DeckCloseEvent;
import net.tinetwork.tradingcards.api.events.DeckItemInteractEvent;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.collector.CollectorBookCardKey;
import net.tinetwork.tradingcards.tradingcardsplugin.collector.CollectorBookManager;
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
    private final CollectorBookManager collectorBookManager;
    private final Storage<TradingCard> deckStorage;

    public DeckListener(final TradingCards plugin) {
        super(plugin);
        this.deckManager = plugin.getDeckManager();
        this.collectorBookManager = plugin.getCollectorBookManager();
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

        final List<ItemStack> inventoryContents = Arrays.stream(e.getInventory().getContents())
                .filter(Objects::nonNull)
                .toList();

        final List<StorageEntry> serializedEntries = serializeDeckEntries(player, inventoryContents);
        if (plugin.getGeneralConfig().collectorBookEnabled()) {
            reconcileCollectorBook(player, serializedEntries);
        }

        final Deck deck = new Deck(player.getUniqueId(),deckNum,serializedEntries);
        deckStorage.saveDeck(player.getUniqueId(), deckNum, deck);
        deckManager.removeDeckViewer(e.getPlayer().getUniqueId());
        debug(InternalDebug.DeckListener.DECK_CLOSED);
    }

    private @NotNull List<StorageEntry> serializeDeckEntries(final @NotNull Player player, final @NotNull List<ItemStack> inventoryContents) {
        final List<StorageEntry> serializedEntries = new ArrayList<>();
        for (ItemStack item : inventoryContents) {
            if (!CardUtil.isCard(item)) {
                if (plugin.getGeneralConfig().dropDeckItems()) {
                    CardUtil.dropItem(player, item);
                }
                continue;
            }

            final StorageEntry entry = formatEntryString(item);
            if (!updateSimilarEntry(serializedEntries, entry)) {
                serializedEntries.add(entry);
            }
            debug(entry.toString());
            debug(InternalDebug.DeckListener.ADDED_ENTRY.formatted(entry));
        }
        return serializedEntries;
    }

    private void reconcileCollectorBook(final @NotNull Player player, final @NotNull List<StorageEntry> currentEntries) {
        final UUID playerUuid = player.getUniqueId();
        final Map<CollectorBookCardKey, Integer> before = deckManager.consumeCollectorDeckSnapshot(playerUuid);
        final Map<CollectorBookCardKey, Integer> after = toCardAmountMap(currentEntries);

        final Set<CollectorBookCardKey> cardKeys = new HashSet<>();
        cardKeys.addAll(before.keySet());
        cardKeys.addAll(after.keySet());

        for (CollectorBookCardKey key : cardKeys) {
            final int beforeAmount = before.getOrDefault(key, 0);
            final int afterAmount = after.getOrDefault(key, 0);
            final int delta = afterAmount - beforeAmount;
            if (delta == 0) {
                continue;
            }

            if (delta > 0) {
                collectorBookManager.addCard(playerUuid, key.cardId(), key.rarityId(), key.seriesId(), key.shiny(), delta);
            } else {
                collectorBookManager.removeCard(playerUuid, key.cardId(), key.rarityId(), key.seriesId(), key.shiny(), -delta);
            }
        }
    }

    private @NotNull Map<CollectorBookCardKey, Integer> toCardAmountMap(final @NotNull List<StorageEntry> entries) {
        final Map<CollectorBookCardKey, Integer> cardAmounts = new HashMap<>();
        for (StorageEntry entry : entries) {
            if (entry.getAmount() <= 0) {
                continue;
            }
            cardAmounts.put(CollectorBookCardKey.fromStorageEntry(entry), entry.getAmount());
        }
        return cardAmounts;
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
        final String cardId = NbtUtils.Card.getCardId(itemStack);
        final String rarity = NbtUtils.Card.getRarityId(itemStack);
        final boolean shiny = NbtUtils.Card.isShiny(itemStack);
        final String seriesId = NbtUtils.Card.getSeriesId(itemStack);
        return new StorageEntry(rarity, cardId, itemStack.getAmount(), shiny,seriesId);
    }
}
