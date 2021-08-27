package net.tinetwork.tradingcards.api.model;

public class Pack {
    private final int numNormalCards;
    private final String normalCardRarity;
    private final int numSpecialCards;
    private final String specialCardsRarity;
    private final int numExtraCards;
    private final String extraCardsRarity;
    private final String series;
    private final double price;
    private final String permissions;

    public Pack(int numNormalCards, String normalCardRarity, int numSpecialCards, String specialCardsRarity, int numExtraCards, String extraCardsRarity, String series, double price, String permissions) {
        this.numNormalCards = numNormalCards;
        this.normalCardRarity = normalCardRarity;
        this.numSpecialCards = numSpecialCards;
        this.specialCardsRarity = specialCardsRarity;
        this.numExtraCards = numExtraCards;
        this.extraCardsRarity = extraCardsRarity;
        this.series = series;
        this.price = price;
        this.permissions = permissions;
    }

    public int getNumNormalCards() {
        return numNormalCards;
    }

    public String getNormalCardRarity() {
        return normalCardRarity;
    }

    public int getNumSpecialCards() {
        return numSpecialCards;
    }

    public String getSpecialCardsRarity() {
        return specialCardsRarity;
    }

    public int getNumExtraCards() {
        return numExtraCards;
    }

    public String getExtraCardsRarity() {
        return extraCardsRarity;
    }

    public String getSeries() {
        return series;
    }

    public double getPrice() {
        return price;
    }

    public String getPermissions() {
        return permissions;
    }
}