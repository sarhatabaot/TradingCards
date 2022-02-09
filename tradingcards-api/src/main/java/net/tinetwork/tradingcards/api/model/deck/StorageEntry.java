package net.tinetwork.tradingcards.api.model.deck;

import java.util.Objects;

public class StorageEntry {
    private final String rarityId;
    private final String cardId;
    private int amount;
    private final boolean isShiny;

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

    public StorageEntry(final String rarityId, final String cardId, final int amount, final boolean isShiny) {
        this.rarityId = rarityId;
        this.cardId = cardId;
        this.amount = amount;
        this.isShiny = isShiny;
    }

    @Override
    public String toString() {
        return rarityId + "," + cardId + "," + amount + "," + isShiny;
    }

    public static StorageEntry fromString(final String string) {
        String[] split = string.split(",");
        final String rarity = split[0];
        final String card = split[1];
        final int amount = Integer.parseInt(split[2]);
        final boolean isShiny = parseShinyString(split[3]);
        return new StorageEntry(rarity, card, amount, isShiny);
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
        return amount == that.amount && isShiny == that.isShiny && Objects.equals(rarityId, that.rarityId) && Objects.equals(cardId, that.cardId);
    }

    public boolean isSimilar(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StorageEntry that = (StorageEntry) o;
        return isShiny == that.isShiny && Objects.equals(rarityId, that.rarityId) && Objects.equals(cardId, that.cardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rarityId, cardId, amount, isShiny);
    }
}
