package net.tinetwork.tradingcards.api.model.pack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
public record PackEntry(String rarityId, int amount, String seriesId) {

    public String getRarityId() {
        return rarityId;
    }

    public int getAmount() {
        return amount;
    }

    public String getSeries() {
        return seriesId;
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return rarityId + ":" + amount
                + ((seriesId != null) ? ":" + seriesId : "");
    }

    @Contract("_ -> new")
    public static @NotNull PackEntry fromString(final @NotNull String string) {
        final String[] split = string.split(":");
        final String rarityId = split[0];
        final int amount = Integer.parseInt(split[1]);
        String seriesId = null;
        if (split.length > 2)
            seriesId = split[2];
        return new PackEntry(rarityId, amount, seriesId);
    }
}
