package net.tinetwork.tradingcards.tradingcardsplugin.managers.cards;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
public record CompositeRaritySeriesKey(String rarityId, String seriesId) {
    @Contract("_ -> new")
    public static @NotNull CompositeRaritySeriesKey fromString(final @NotNull String key) {
        String[] splitKey = key.split("\\.");
        return new CompositeRaritySeriesKey(splitKey[0],splitKey[1]);
    }

    @Contract(pure = true)
    public @NotNull String toString() {
        return rarityId+"."+seriesId;
    }

    @Contract("_, _ -> new")
    public static @NotNull CompositeRaritySeriesKey of(final String rarityId, final String seriesId) {
        return new CompositeRaritySeriesKey(rarityId,seriesId);
    }

}
