package net.tinetwork.tradingcards.api.model.deck;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
public class StorageEntry {
    private final String rarityId;
    private final String cardId;
    private int amount;
    private final boolean isShiny;
    private final String seriesId;

    public String getRarityId() {
        return rarityId;
    }

    public String getCardId() {
        return cardId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isShiny() {
        return isShiny;
    }

    public StorageEntry(final String rarityId, final String cardId, final int amount, final boolean isShiny,final String seriesId) {
        this.rarityId = rarityId;
        this.cardId = cardId;
        this.amount = amount;
        this.isShiny = isShiny;
        this.seriesId = seriesId;
    }

    @Override
    public String toString() {
        return rarityId + "," + cardId + "," + amount + "," + isShiny+","+seriesId;
    }

    public static @NotNull StorageEntry fromString(final @NotNull String string) {
        String[] split = string.split(",");
        final String rarity = split[0];
        final String card = split[1];
        final int amount = Integer.parseInt(split[2]);
        final boolean isShiny = parseShinyString(split[3]);
        final String seriesId = split[4];
        return new StorageEntry(rarity, card, amount, isShiny,seriesId);
    }

    private static boolean parseShinyString(final String string) {
        if("no".equalsIgnoreCase(string))
            return false;
        if("yes".equalsIgnoreCase(string))
            return true;
        return Boolean.parseBoolean(string);
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StorageEntry that = (StorageEntry) o;
        return amount == that.amount && isShiny == that.isShiny && Objects.equals(rarityId, that.rarityId) && Objects.equals(cardId, that.cardId) && Objects.equals(seriesId,that.getSeriesId());
    }

    public boolean isSimilar(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StorageEntry that = (StorageEntry) o;
        return isShiny == that.isShiny && Objects.equals(rarityId, that.rarityId) && Objects.equals(cardId, that.cardId) && Objects.equals(seriesId,that.getSeriesId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(rarityId, cardId, amount, isShiny,seriesId);
    }

    public String getSeriesId() {
        return seriesId;
    }
}
