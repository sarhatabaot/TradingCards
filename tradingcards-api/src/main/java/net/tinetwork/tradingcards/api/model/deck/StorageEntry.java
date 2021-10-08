package net.tinetwork.tradingcards.api.model.deck;

public record StorageEntry(String rarityId, String cardId, int amount, boolean isShiny) {

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
}
