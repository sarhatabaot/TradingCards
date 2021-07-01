package net.tinetwork.tradingcards.api.manager;

import net.tinetwork.tradingcards.api.card.Card;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CardManager<T> {
    Map<String, Card<T>> getCards();
    Map<String, Card<T>> getActiveCards();

    Card<T> getCard(final String cardName, final String rarity, final boolean forcedShiny);
    Card<T> getActiveCard(final String cardName, final String rarity, final boolean forcedShiny);
    Card<T> getRandomCard(final String rarity, final boolean forcedShiny);
    Card<T> getRandomActiveCard(final String rarity, final boolean forcedShiny);
    ItemStack getCardItem(final String cardName, final String rarity, int num);

    List<String> getRarityCardList(final String rarity);

    Set<String> getRarityNames();
}