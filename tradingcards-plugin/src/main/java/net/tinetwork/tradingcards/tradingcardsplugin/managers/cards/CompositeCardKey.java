package net.tinetwork.tradingcards.tradingcardsplugin.managers.cards;

import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
public record CompositeCardKey (String rarityId, String seriesId, String cardId){
    @Contract("_ -> new")
    public static @NotNull CompositeCardKey fromString(final @NotNull String key) {
        String[] splitKey = key.split("\\.");
        return new CompositeCardKey(splitKey[0],splitKey[1],splitKey[2]);
    }

    @Contract(pure = true)
    public @NotNull String toString() {
        return rarityId+"."+seriesId+"."+cardId;
    }

    @Contract("_ -> new")
    public static @NotNull CompositeCardKey fromCard(@NotNull TradingCard card) {
        return new CompositeCardKey(card.getRarity().getId(),card.getSeries().getId(),card.getCardId());
    }
}
