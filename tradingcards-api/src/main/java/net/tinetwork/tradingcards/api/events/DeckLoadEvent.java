package net.tinetwork.tradingcards.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class DeckLoadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Inventory inventory;
    private final int deckNumber;

    public DeckLoadEvent(final Inventory inventory, final int deckNumber) {
        this.inventory = inventory;
        this.deckNumber = deckNumber;
    }

    public Inventory getInventory() {
        return inventory;
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
