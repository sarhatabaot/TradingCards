package net.tinetwork.tradingcards.api.events;

import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public class DeckOpenEvent extends InventoryOpenEvent {
    private static final HandlerList handlers = new HandlerList();
    private final int deckNumber;

    public DeckOpenEvent(@NotNull InventoryView transaction, int deckNumber) {
        super(transaction);
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
