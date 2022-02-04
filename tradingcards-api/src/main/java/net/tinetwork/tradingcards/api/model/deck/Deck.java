package net.tinetwork.tradingcards.api.model.deck;

import java.util.*;

public class Deck {
    private final UUID playerUuid;
    private final int number;
    private final List<StorageEntry> deckEntries;

    public Deck(UUID playerUuid, int number, List<StorageEntry> deckEntries) {
        this.playerUuid = playerUuid;
        this.number = number;
        this.deckEntries = deckEntries;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public int getNumber() {
        return number;
    }

    public List<StorageEntry> getDeckEntries() {
        return deckEntries;
    }

    public StorageEntry getEntryList(int num) {
        return deckEntries.get(num);
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Deck deck = (Deck) o;
        return number == deck.number && Objects.equals(playerUuid, deck.playerUuid) && Objects.equals(deckEntries, deck.deckEntries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerUuid, number, deckEntries);
    }

    public boolean containsCard(final String cardId, final String rarityId) {
        for(StorageEntry entry: deckEntries) {
            if(entry.getCardId().equals(cardId) && entry.getRarityId().equals(rarityId))
                return true;
        }
        return false;
    }
}
