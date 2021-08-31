package net.tinetwork.tradingcards.api.model.deck;

public record DeckEntry(String rarityId, String cardId, int amount, boolean isShiny) {

    public String getRarityId() {
        return rarityId;
    }

    public String getCardId() {
        return cardId;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return rarityId + "," + cardId + "," + amount + "," + isShiny;
    }

    public static DeckEntry fromString(final String string) {
        String[] split = string.split(",");
        final String rarity = split[0];
        final String card = split[1];
        final int amount = Integer.parseInt(split[2]);
        final boolean isShiny = Boolean.parseBoolean(split[3]);
        return new DeckEntry(rarity, card, amount, isShiny);
    }
}
