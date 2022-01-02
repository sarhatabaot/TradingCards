package net.tinetwork.tradingcards.tradingcardsplugin.storage;

import net.tinetwork.tradingcards.api.model.deck.Deck;

import java.util.List;
import java.util.UUID;

/**
 * @author sarhatabaot
 */
public interface Storage {

    /**
     *
     * @param playerUuid Player UUID
     * @return a List of decks the player has.
     */
    List<Deck> getPlayerDecks(UUID playerUuid);

    /**
     *
     * @param playerUuid Player UUID
     * @param deckNumber Deck number, must be larger than 1
     * @return Return a corresponding deck object
     */
    Deck getDeck(UUID playerUuid, int deckNumber);

    /**
     * Saves the deck to storage.
     * @param playerUuid Player UUID
     * @param deckNumber Deck number, must be larger than 1
     * @param deck Corresponding deck object.
     */
    void save(UUID playerUuid, int deckNumber, Deck deck);

    boolean hasCard(UUID playerUuid, String card, String rarity);

    boolean hasShinyCard(UUID playerUuid, String card, String rarity);

    /**
     * Returns the storage type used.
     * @return StorageType
     */
    StorageType getType();
}
