package net.tinetwork.tradingcards.tradingcardsplugin.collector;

import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class CollectorBookManager {
    // Reserved deck number used as canonical collector-book storage.
    public static final int COLLECTOR_BOOK_DECK_NUMBER = 0;

    public record CollectorMigrationSummary(
            UUID playerUuid,
            boolean dryRun,
            boolean force,
            boolean skippedByGuard,
            boolean collectorBookHadEntries,
            int legacyDeckCount,
            int legacyOwnershipEntries,
            boolean collectorBookMigrationNeeded,
            boolean collectorBookMigrated,
            int legacyDecksNeedingNormalization
    ) {
        public boolean hasWork() {
            return collectorBookMigrationNeeded || legacyDecksNeedingNormalization > 0;
        }
    }

    private final TradingCards plugin;
    private final Storage<TradingCard> storage;
    private final Set<UUID> migrationCheckedPlayers;

    public CollectorBookManager(final @NotNull TradingCards plugin) {
        this.plugin = plugin;
        this.storage = plugin.getStorage();
        this.migrationCheckedPlayers = new HashSet<>();
    }

    public @NotNull CollectorBook getBook(final @NotNull UUID playerUuid) {
        ensureCollectorBookMigrated(playerUuid);
        final Deck deck = storage.getDeck(playerUuid, COLLECTOR_BOOK_DECK_NUMBER);
        if (deck == null) {
            return new CollectorBook(playerUuid);
        }

        return CollectorBook.fromEntries(playerUuid, deck.getDeckEntries());
    }

    public void saveBook(final @NotNull CollectorBook collectorBook) {
        final Deck bookDeck = new Deck(
                collectorBook.getPlayerUuid(),
                COLLECTOR_BOOK_DECK_NUMBER,
                collectorBook.toEntries()
        );
        storage.saveDeck(collectorBook.getPlayerUuid(), COLLECTOR_BOOK_DECK_NUMBER, bookDeck);
    }

    public int addCard(final @NotNull UUID playerUuid, final @NotNull StorageEntry storageEntry) {
        final CollectorBook collectorBook = getBook(playerUuid);
        final int amount = collectorBook.add(storageEntry);
        saveBook(collectorBook);
        return amount;
    }

    public int addCard(
            final @NotNull UUID playerUuid,
            final @NotNull String cardId,
            final @NotNull String rarityId,
            final @NotNull String seriesId,
            final boolean shiny,
            final int amount
    ) {
        final CollectorBook collectorBook = getBook(playerUuid);
        final int newAmount = collectorBook.add(cardId, rarityId, seriesId, shiny, amount);
        saveBook(collectorBook);
        return newAmount;
    }

    public int removeCard(
            final @NotNull UUID playerUuid,
            final @NotNull String cardId,
            final @NotNull String rarityId,
            final @NotNull String seriesId,
            final boolean shiny,
            final int amount
    ) {
        final CollectorBook collectorBook = getBook(playerUuid);
        final int removedAmount = collectorBook.remove(cardId, rarityId, seriesId, shiny, amount);
        saveBook(collectorBook);
        return removedAmount;
    }

    public int getOwnedAmount(
            final @NotNull UUID playerUuid,
            final @NotNull String cardId,
            final @NotNull String rarityId,
            final @NotNull String seriesId,
            final boolean shiny
    ) {
        return getBook(playerUuid).getAmount(cardId, rarityId, seriesId, shiny);
    }

    public boolean hasCard(
            final @NotNull UUID playerUuid,
            final @NotNull String cardId,
            final @NotNull String rarityId,
            final @NotNull String seriesId
    ) {
        return getOwnedEntries(playerUuid).stream()
                .anyMatch(entry ->
                        !entry.isShiny() &&
                                entry.getCardId().equals(cardId) &&
                                entry.getRarityId().equals(rarityId) &&
                                entry.getSeriesId().equals(seriesId) &&
                                entry.getAmount() > 0);
    }

    public boolean hasShinyCard(
            final @NotNull UUID playerUuid,
            final @NotNull String cardId,
            final @NotNull String rarityId,
            final @NotNull String seriesId
    ) {
        return getOwnedEntries(playerUuid).stream()
                .anyMatch(entry ->
                        entry.isShiny() &&
                                entry.getCardId().equals(cardId) &&
                                entry.getRarityId().equals(rarityId) &&
                                entry.getSeriesId().equals(seriesId) &&
                                entry.getAmount() > 0);
    }

    public @NotNull List<StorageEntry> getOwnedEntries(final @NotNull UUID playerUuid) {
        final List<StorageEntry> bookEntries = getBook(playerUuid).toEntries();
        if (!bookEntries.isEmpty()) {
            return bookEntries;
        }

        return getLegacyDeckEntries(playerUuid);
    }

    private @NotNull List<StorageEntry> getLegacyDeckEntries(final @NotNull UUID playerUuid) {
        return getMergedLegacyDeckEntries(storage.getPlayerDecks(playerUuid));
    }

    private @NotNull List<StorageEntry> getMergedLegacyDeckEntries(final @NotNull List<Deck> decks) {
        final Map<CollectorBookCardKey, Integer> mergedEntries = new HashMap<>();

        for (Deck deck : decks) {
            if (deck == null || deck.getNumber() == COLLECTOR_BOOK_DECK_NUMBER || deck.getDeckEntries() == null) {
                continue;
            }

            for (StorageEntry entry : deck.getDeckEntries()) {
                if (entry == null || entry.getAmount() <= 0) {
                    continue;
                }

                final CollectorBookCardKey key = CollectorBookCardKey.fromStorageEntry(entry);
                mergedEntries.merge(key, entry.getAmount(), Integer::sum);
            }
        }

        final List<StorageEntry> entries = new ArrayList<>();
        for (Map.Entry<CollectorBookCardKey, Integer> mergedEntry : mergedEntries.entrySet()) {
            final CollectorBookCardKey key = mergedEntry.getKey();
            entries.add(new StorageEntry(
                    key.rarityId(),
                    key.cardId(),
                    mergedEntry.getValue(),
                    key.shiny(),
                    key.seriesId()
            ));
        }

        return entries;
    }

    public void ensureCollectorBookMigrated(final @NotNull UUID playerUuid) {
        migratePlayer(playerUuid, false, false);
    }

    public @NotNull CollectorMigrationSummary migratePlayer(
            final @NotNull UUID playerUuid,
            final boolean force,
            final boolean dryRun
    ) {
        final boolean alreadyChecked = migrationCheckedPlayers.contains(playerUuid);
        final boolean skippedByGuard = alreadyChecked && !force;
        final boolean applyChanges = !dryRun && !skippedByGuard;

        plugin.debug(CollectorBookManager.class, "Collector migration started for %s (force=%s,dryRun=%s,skipped=%s)"
                .formatted(playerUuid, force, dryRun, skippedByGuard));

        final List<Deck> playerDecks = storage.getPlayerDecks(playerUuid);
        final Deck collectorDeck = storage.getDeck(playerUuid, COLLECTOR_BOOK_DECK_NUMBER);
        final List<Deck> legacyDecks = playerDecks.stream()
                .filter(Objects::nonNull)
                .filter(deck -> deck.getNumber() != COLLECTOR_BOOK_DECK_NUMBER)
                .toList();
        final List<StorageEntry> legacyEntries = getMergedLegacyDeckEntries(legacyDecks);

        final boolean collectorBookHadEntries = hasPositiveEntries(collectorDeck == null ? null : collectorDeck.getDeckEntries());
        final boolean collectorBookMigrationNeeded = !collectorBookHadEntries && !legacyEntries.isEmpty();
        boolean collectorBookMigrated = false;
        if (collectorBookMigrationNeeded) {
            if (applyChanges) {
                saveBook(CollectorBook.fromEntries(playerUuid, legacyEntries));
                collectorBookMigrated = true;
                plugin.debug(CollectorBookManager.class, "Migrated %d ownership entries into collector book for %s"
                        .formatted(legacyEntries.size(), playerUuid));
            } else {
                plugin.debug(CollectorBookManager.class, "Collector book migration needed for %s, but changes were not applied"
                        .formatted(playerUuid));
            }
        }

        final int legacyDecksNeedingNormalization = normalizeLegacyDeckReferences(playerUuid, legacyDecks, applyChanges);

        if (applyChanges) {
            migrationCheckedPlayers.add(playerUuid);
        }

        final CollectorMigrationSummary summary = new CollectorMigrationSummary(
                playerUuid,
                dryRun,
                force,
                skippedByGuard,
                collectorBookHadEntries,
                legacyDecks.size(),
                legacyEntries.size(),
                collectorBookMigrationNeeded,
                collectorBookMigrated,
                legacyDecksNeedingNormalization
        );

        plugin.debug(CollectorBookManager.class, "Collector migration finished for %s (work=%s,migratedBook=%s,normalizedDecks=%d)"
                .formatted(playerUuid, summary.hasWork(), summary.collectorBookMigrated(), summary.legacyDecksNeedingNormalization()));
        return summary;
    }

    private int normalizeLegacyDeckReferences(
            final @NotNull UUID playerUuid,
            final @NotNull List<Deck> legacyDecks,
            final boolean applyChanges
    ) {
        int changedDecks = 0;
        for (Deck legacyDeck : legacyDecks) {
            if (legacyDeck.getDeckEntries() == null) {
                continue;
            }

            final List<StorageEntry> referenceEntries = toReferenceEntries(legacyDeck.getDeckEntries());
            if (legacyDeck.getDeckEntries().equals(referenceEntries)) {
                continue;
            }

            changedDecks++;
            if (applyChanges) {
                plugin.debug(CollectorBookManager.class, "Normalizing deck #%d for %s from %d -> %d entries"
                        .formatted(legacyDeck.getNumber(), playerUuid, legacyDeck.getDeckEntries().size(), referenceEntries.size()));
                storage.saveDeck(playerUuid, legacyDeck.getNumber(), new Deck(playerUuid, legacyDeck.getNumber(), referenceEntries));
            }
        }
        return changedDecks;
    }

    private @NotNull List<StorageEntry> toReferenceEntries(final @NotNull List<StorageEntry> deckEntries) {
        final Map<CollectorBookCardKey, StorageEntry> uniqueEntries = new LinkedHashMap<>();

        for (StorageEntry entry : deckEntries) {
            if (entry == null || entry.getAmount() <= 0) {
                continue;
            }

            final CollectorBookCardKey key = CollectorBookCardKey.fromStorageEntry(entry);
            uniqueEntries.putIfAbsent(key, new StorageEntry(
                    entry.getRarityId(),
                    entry.getCardId(),
                    1,
                    entry.isShiny(),
                    entry.getSeriesId()
            ));
        }

        return new ArrayList<>(uniqueEntries.values());
    }

    private boolean hasPositiveEntries(final List<StorageEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return false;
        }

        for (StorageEntry entry : entries) {
            if (entry != null && entry.getAmount() > 0) {
                return true;
            }
        }

        return false;
    }
}
