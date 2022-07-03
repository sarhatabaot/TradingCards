package net.tinetwork.tradingcards.api.manager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author sarhatabaot
 */
public interface DeckManager {
    /**
     * @param player Player
     * @param deckNum Deck Number
     */
    void openDeck(@NotNull final Player player, final int deckNum);

    /**
     * @param player Player
     * @param deckNumber Deck number
     * @return the created deck item
     */
    @NotNull
    ItemStack createDeckItem(@NotNull final Player player, final int deckNumber);

    /**
     * @param player Player
     * @param deckNumber Deck Number
     * @return the created item
     */
    @NotNull
    ItemStack getNbtItem(@NotNull final Player player, final int deckNumber);

    /**
     * @param item Item
     * @return Deck Item
     */
    boolean isDeck(final ItemStack item);

    /**
     * @param player Player
     * @param num Deck Number
     * @return true if the player has the deck item
     */
    boolean hasDeckItem(@NotNull final Player player, final int num);

    /**
     * @param player Player
     * @param cardId Card Id
     * @param rarityId Rarity Id
     * @return true if the player has the card
     */
    boolean hasCard(final Player player, final String cardId, final String rarityId, final String seriesId);

    /**
     * @param player player
     * @param cardId card id
     * @param rarityId rarity id
     * @param seriesId series id
     * @return If the player has a shiny card.
     */
    boolean hasShinyCard(final Player player, final String cardId, final String rarityId, final String seriesId);

    /**
     * @param uuid uuid
     * @param deckNumber deck number
     */
    void createNewDeckInFile(final UUID uuid, final int deckNumber);
}
