package net.tinetwork.tradingcards.api.manager;

import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.model.DropType;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CardManager<T extends Card<T>> {
    /**
     * @return a map of cards and names.
     */
    Map<String, T> getCards();

    /**
     * @return a list of active cards.
     */
    List<String> getActiveCards();

    /**
     * @param cardId The card id.
     * @param rarityId The rarity id.
     * @param forcedShiny Force the card to be shiny.
     * @return The card.
     */
    T getCard(final String cardId, final String rarityId, final boolean forcedShiny);

    /**
     * @param cardId The card id.
     * @param rarityId The rarity id.
     * @param forcedShiny Force the card to be shiny.
     * @return The card.
     */
    T getActiveCard(final String cardId, final String rarityId, final boolean forcedShiny);
    /**
     * @param cardId The card id.
     * @param rarityId The rarity id.
     * @return The card.
     */
    T getActiveCard(final String cardId, final String rarityId);

    /**
     * @param rarityId The rarity id.
     * @param forcedShiny  Force the card to be shiny.
     * @return A random card from a rarity.
     */
    T getRandomCard(final String rarityId, final boolean forcedShiny);

    /**
     * @param rarityId The rarity id.
     * @return A random card from a rarity.
     */
    T getRandomCard(final String rarityId);

    /**
     * @param rarityId The rarity id.
     * @param forcedShiny  Force the card to be shiny.
     * @return A random active card.
     */
    T getRandomActiveCard(final String rarityId, final boolean forcedShiny);

    /**
     * @param rarityId The rarity id.
     * @return A random active card.
     */
    T getRandomActiveCard(final String rarityId);


    /**
     * @param dropType DropType
     * @param alwaysDrop Should the card always drop.
     * @return A random rarity
     */
    String getRandomRarity(final DropType dropType, boolean alwaysDrop);

    /**
     * @param rarityId The rarity id
     * @return returns a list of cards from a rarity.
     */
    List<T> getRarityCardList(final String rarityId);

    /**
     * @param rarityId The rarity id
     * @return returns a list of cards names from a rarity.
     */
    List<String> getRarityCardListNames(final String rarityId);

    /**
     * @param rarity Rarity
     * @return returns a list of actives card names from an active rarity.
     */
    List<String> getActiveRarityCardList(final String rarity);

    /**
     * @return A set of all rarity names.
     */
    Set<String> getRarityNames();

    /**
     * @return A set of active rarity names.
     */
    Set<String> getActiveRarityNames();

    boolean containsCard(final String rarityId, final String cardId, final String seriesId);
}