package net.tinetwork.tradingcards.tradingcardsplugin.collector;

import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CollectorBook {
    private final UUID playerUuid;
    private final Map<CollectorBookCardKey, Integer> cardAmounts;

    public CollectorBook(final @NotNull UUID playerUuid) {
        this(playerUuid, new HashMap<>());
    }

    public CollectorBook(final @NotNull UUID playerUuid, final @NotNull Map<CollectorBookCardKey, Integer> cardAmounts) {
        this.playerUuid = playerUuid;
        this.cardAmounts = new HashMap<>(cardAmounts);
    }

    public static @NotNull CollectorBook fromEntries(final @NotNull UUID playerUuid, final List<StorageEntry> entries) {
        final CollectorBook collectorBook = new CollectorBook(playerUuid);
        if (entries == null || entries.isEmpty()) {
            return collectorBook;
        }

        for (final StorageEntry entry : entries) {
            collectorBook.add(entry);
        }
        return collectorBook;
    }

    public @NotNull UUID getPlayerUuid() {
        return playerUuid;
    }

    public int add(final @NotNull StorageEntry entry) {
        return add(
                entry.getCardId(),
                entry.getRarityId(),
                entry.getSeriesId(),
                entry.isShiny(),
                entry.getAmount()
        );
    }

    public int add(
            final @NotNull String cardId,
            final @NotNull String rarityId,
            final @NotNull String seriesId,
            final boolean shiny,
            final int amount
    ) {
        if (amount <= 0) {
            return 0;
        }

        final CollectorBookCardKey key = new CollectorBookCardKey(rarityId, seriesId, cardId, shiny);
        cardAmounts.merge(key, amount, Integer::sum);
        return cardAmounts.getOrDefault(key, 0);
    }

    public int remove(
            final @NotNull String cardId,
            final @NotNull String rarityId,
            final @NotNull String seriesId,
            final boolean shiny,
            final int amount
    ) {
        if (amount <= 0) {
            return 0;
        }

        final CollectorBookCardKey key = new CollectorBookCardKey(rarityId, seriesId, cardId, shiny);
        final int current = cardAmounts.getOrDefault(key, 0);
        if (current <= 0) {
            return 0;
        }

        final int newAmount = Math.max(0, current - amount);
        if (newAmount == 0) {
            cardAmounts.remove(key);
        } else {
            cardAmounts.put(key, newAmount);
        }

        return current - newAmount;
    }

    public int getAmount(
            final @NotNull String cardId,
            final @NotNull String rarityId,
            final @NotNull String seriesId,
            final boolean shiny
    ) {
        return cardAmounts.getOrDefault(new CollectorBookCardKey(rarityId, seriesId, cardId, shiny), 0);
    }

    public boolean hasCard(
            final @NotNull String cardId,
            final @NotNull String rarityId,
            final @NotNull String seriesId,
            final boolean shiny
    ) {
        return getAmount(cardId, rarityId, seriesId, shiny) > 0;
    }

    public @NotNull List<StorageEntry> toEntries() {
        final List<StorageEntry> entries = new ArrayList<>();
        for (Map.Entry<CollectorBookCardKey, Integer> cardEntry : cardAmounts.entrySet()) {
            final int amount = cardEntry.getValue();
            if (amount <= 0) {
                continue;
            }

            final CollectorBookCardKey key = cardEntry.getKey();
            entries.add(new StorageEntry(
                    key.rarityId(),
                    key.cardId(),
                    amount,
                    key.shiny(),
                    key.seriesId()
            ));
        }

        entries.sort(Comparator
                .comparing(StorageEntry::getRarityId)
                .thenComparing(StorageEntry::getSeriesId)
                .thenComparing(StorageEntry::getCardId)
                .thenComparing(StorageEntry::isShiny));
        return entries;
    }
}
