package net.tinetwork.tradingcards.api.events;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author sarhatabaot
 */
public class DeckItemInteractEvent extends PlayerInteractEvent {
    private static final HandlerList handlers = new HandlerList();
    private final int deckNumber;

    public DeckItemInteractEvent(final @NotNull Player who, final @NotNull Action action, final @Nullable ItemStack item, final @Nullable Block clickedBlock, final @NotNull BlockFace clickedFace, final int deckNumber) {
        super(who, action, item, clickedBlock, clickedFace);
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
