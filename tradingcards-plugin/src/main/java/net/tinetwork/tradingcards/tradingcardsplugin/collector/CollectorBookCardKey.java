package net.tinetwork.tradingcards.tradingcardsplugin.collector;

import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import org.jetbrains.annotations.NotNull;

public record CollectorBookCardKey(
        String rarityId,
        String seriesId,
        String cardId,
        boolean shiny
) {
    public static @NotNull CollectorBookCardKey fromStorageEntry(final @NotNull StorageEntry storageEntry) {
        return new CollectorBookCardKey(
                storageEntry.getRarityId(),
                storageEntry.getSeriesId(),
                storageEntry.getCardId(),
                storageEntry.isShiny()
        );
    }
}
