package net.tinetwork.tradingcards.api.manager;

import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.model.DropType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CardManager<T extends Card<T>> {
    /**
     *
     * @return
     */
    Map<String, T> getCards();

    /**
     *
     * @return
     */
    List<String> getActiveCards();

    /**
     *
     * @param cardName
     * @param rarity
     * @param forcedShiny
     * @return
     */
    T getCard(final String cardName, final String rarity, final boolean forcedShiny);

    /**
     *
     * @param cardName
     * @param rarity
     * @param forcedShiny
     * @return
     */
    T getActiveCard(final String cardName, final String rarity, final boolean forcedShiny);
    T getActiveCard(final String cardName, final String rarity);
    /**
     *
     * @param rarity
     * @param forcedShiny
     * @return
     */
    T getRandomCard(final String rarity, final boolean forcedShiny);
    T getRandomCard(final String rarity);

    /**
     *
     * @param rarity
     * @param forcedShiny
     * @return
     */
    T getRandomActiveCard(final String rarity, final boolean forcedShiny);

    T getRandomActiveCard(final String rarity);


    /**
     *
     * @param dropType
     * @param alwaysDrop
     * @return
     */
    String getRandomRarity(final DropType dropType, boolean alwaysDrop);

    /**
     * @param rarity Rarity
     * @return returns a list of card names from rarity.
     */
    List<T> getRarityCardList(final String rarity);

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
}