package net.tinetwork.tradingcards.api.card;

/**
 * @author sarhatabaot
 */
public class CardMeta {
    private boolean playerCard;

    private String displayName;
    private String about;
    private String info;

    private int customModelNbt;
    private double buyPrice;
    private double sellPrice;
    private String currencyId;

    private boolean shiny;


    public CardMeta(final boolean playerCard, final String displayName, final String about, final String info, final int customModelNbt, final double buyPrice, final double sellPrice, final boolean shiny) {
        this.playerCard = playerCard;
        this.displayName = displayName;
        this.about = about;
        this.info = info;
        this.customModelNbt = customModelNbt;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.shiny = shiny;
    }

    public CardMeta() {
    }

    public void setPlayerCard(final boolean playerCard) {
        this.playerCard = playerCard;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public void setAbout(final String about) {
        this.about = about;
    }

    public void setInfo(final String info) {
        this.info = info;
    }

    public void setCustomModelNbt(final int customModelNbt) {
        this.customModelNbt = customModelNbt;
    }

    public void setBuyPrice(final double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public void setSellPrice(final double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public void setShiny(final boolean shiny) {
        this.shiny = shiny;
    }

    public boolean isPlayerCard() {
        return playerCard;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAbout() {
        return about;
    }

    public String getInfo() {
        return info;
    }

    public int getCustomModelNbt() {
        return customModelNbt;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public boolean isShiny() {
        return shiny;
    }

    public void setCurrencyId(final String currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    @Override
    public String toString() {
        return "CardMeta{" +
                "playerCard=" + playerCard +
                ", displayName='" + displayName + '\'' +
                ", about='" + about + '\'' +
                ", info='" + info + '\'' +
                ", customModelNbt=" + customModelNbt +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                ", shiny=" + shiny +
                '}';
    }
}
