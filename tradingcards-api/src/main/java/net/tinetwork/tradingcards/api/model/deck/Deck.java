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
}
