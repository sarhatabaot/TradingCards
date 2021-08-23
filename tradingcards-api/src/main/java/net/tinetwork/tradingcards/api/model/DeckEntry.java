package net.tinetwork.tradingcards.api.model;

public class DeckEntry {
    private String rarityId;
    private String cardId;
    private int amount;
    private boolean isShiny;

    public DeckEntry(String rarityId, String cardId, int amount, boolean isShiny) {
        this.rarityId = rarityId;
        this.cardId = cardId;
        this.amount = amount;
        this.isShiny = isShiny;
    }

    public String getRarityId() {
        return rarityId;
    }

    public String getCardId() {
        return cardId;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isShiny() {
        return isShiny;
    }

    @Override
    public String toString() {
        return rarityId + ","+cardId+","+amount+","+isShiny;
    }

    public static DeckEntry fromString(final String string) {
        String[] split = string.split(",");
        final String rarity = split[0];
        final String card = split[1];
        final int amount = Integer.parseInt(split[2]);
        final boolean isShiny = Boolean.parseBoolean(split[3]);
        return new DeckEntry(rarity,card,amount,isShiny);
    }
}
