package net.tinetwork.tradingcards.api.manager;

import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.model.MobType;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CardManager<T> {
    /**
     *
     * @return
     */
    Map<String, Card<T>> getCards();

    /**
     *
     *
     * @return
     */
    Map<String, Card<T>> getActiveCards();

    /**
     *
     * @param cardName
     * @param rarity
     * @param forcedShiny
     * @return
     */
    Card<T> getCard(final String cardName, final String rarity, final boolean forcedShiny);

    /**
     *
     * @param cardName
     * @param rarity
     * @param forcedShiny
     * @return
     */
    Card<T> getActiveCard(final String cardName, final String rarity, final boolean forcedShiny);

    /**
     *
     * @param rarity
     * @param forcedShiny
     * @return
     */
    Card<T> getRandomCard(final String rarity, final boolean forcedShiny);

    /**
     *
     * @param rarity
     * @param forcedShiny
     * @return
     */
    Card<T> getRandomActiveCard(final String rarity, final boolean forcedShiny);

    /**
     *
     * @param cardName
     * @param rarity
     * @param num
     * @return
     */
    ItemStack getCardItem(final String cardName, final String rarity, int num);

    /**
     *
     * @param mobType
     * @param alwaysDrop
     * @return
     */
    String getRandomRarity(final MobType mobType, boolean alwaysDrop);

    /**
     * @param rarity Rarity
     * @return returns a list of card names from rarity.
     */
    List<String> getRarityCardList(final String rarity);

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