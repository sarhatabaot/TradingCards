package net.tinetwork.tradingcards.api.manager;

import net.tinetwork.tradingcards.api.card.Card;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public abstract class CardManager<T> {
    public abstract Map<String, Card<T>> getCards();
    public abstract Map<String, Card<T>> getActiveCards();

    public abstract Card<T> getCard(final String cardName,final String rarity, final boolean forcedShiny);
    public abstract Card<T> getActiveCard(final String cardName,final String rarity, final boolean forcedShiny);
    public abstract Card<T> getRandomCard(final String rarity, final boolean forcedShiny);
    public abstract Card<T> getRandomActiveCard(final String rarity, final boolean forcedShiny);
    public abstract ItemStack getCardItem(final String cardName,final String rarity, int num);

}