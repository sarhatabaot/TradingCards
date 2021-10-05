package net.tinetwork.tradingcards.api.events;

import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public class DeckCloseEvent extends InventoryCloseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final int deckNumber;

    public DeckCloseEvent(@NotNull InventoryView transaction, int deckNumber) {
        super(transaction);
        this.deckNumber = deckNumber;
    }

    public DeckCloseEvent(@NotNull InventoryView transaction, @NotNull Reason reason, int deckNumber) {
        super(transaction, reason);
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
