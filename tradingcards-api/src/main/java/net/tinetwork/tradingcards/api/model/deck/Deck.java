package net.tinetwork.tradingcards.api.model.deck;

import java.util.*;

public class Deck {
    private final UUID playerUuid;
    private final int number;
    private final Map<Integer,DeckEntry> deckEntries;

    public Deck(UUID playerUuid, int number) {
        this.playerUuid = playerUuid;
        this.number = number;
        this.deckEntries = new HashMap<>();
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public int getNumber() {
        return number;
    }

    public Map<Integer,DeckEntry> getDeckEntries() {
        return deckEntries;
    }

    public DeckEntry getEntry(int num) {
        return deckEntries.get(num);
    }
}
