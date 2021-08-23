package net.tinetwork.tradingcards.api.model;

import java.util.*;

public class Deck {
    private UUID playerUuid;
    private int number;
    private Map<Integer,DeckEntry> deckEntries;

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
