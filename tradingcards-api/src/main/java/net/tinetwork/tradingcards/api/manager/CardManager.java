package net.tinetwork.tradingcards.api.manager;

import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.model.DropType;

import java.util.List;
import java.util.Set;

public interface CardManager<T extends Card<T>> {

    /**
     * @return a list of active cards.
     */
    List<String> getActiveCards();

    List<String> getCards();

    T getCard(final String cardId, final String rarityId, final String seriesId);

    /**
     * @param cardId The card id.
     * @param rarityId The rarity id.
     * @return The card.
     */
    T getActiveCard(final String cardId, final String rarityId, final String seriesId);

    /**
     * @param rarityId The rarity id.
     * @return A random card from a rarity.
     */
    T getRandomCardByRarity(final String rarityId);

    T getRandomCardBySeries(final String seriesId);


    /**
     * @param rarityId The rarity id.
     * @return A random active card.
     */
    T getRandomActiveCardByRarity(final String rarityId);


    /**
     * @param dropType DropType
     * @param alwaysDrop Should the card always drop.
     * @return A random rarity
     */
    //We might want to get a Rarity object from this.
    String getRandomRarityId(final DropType dropType, boolean alwaysDrop);

    /**
     * @param rarityId The rarity id
     * @return returns a list of cards from a rarity.
     */
    List<T> getRarityCardList(final String rarityId);

    List<T> getSeriesCardList(final String seriesId);
    /**
     * @param rarityId The rarity id
     * @return returns a list of cards names from a rarity.
     */
    List<String> getRarityCardListIds(final String rarityId);

    /**
     * @param rarity Rarity
     * @return returns a list of actives card names from an active rarity.
     */
    List<String> getActiveRarityCardIds(final String rarity);

    List<String> getCardsIdsInRarityAndSeries(final String rarityId, final String seriesId);

    /**
     * @return A set of active rarity names.
     */
    Set<String> getActiveRarityIds();

    boolean containsCard(final String rarityId, final String cardId, final String seriesId);
}