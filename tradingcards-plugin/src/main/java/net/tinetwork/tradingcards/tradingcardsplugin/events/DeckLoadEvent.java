package net.tinetwork.tradingcards.tradingcardsplugin.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DeckLoadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final int deckNumber;

    public DeckLoadEvent(int deckNumber) {
        this.deckNumber = deckNumber;
    }

    public int getDeckNumber() {
        return deckNumber;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
