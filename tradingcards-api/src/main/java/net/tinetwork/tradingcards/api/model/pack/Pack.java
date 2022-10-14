package net.tinetwork.tradingcards.api.model.pack;

import java.util.List;
import java.util.Objects;

public class Pack {
    private final String id;
    private final List<PackEntry> packEntryList;
    private String displayName;
    private double buyPrice;
    private String currencyId;
    private String permission;

    private final List<PackEntry> tradeCards;

    public Pack(final String id, final List<PackEntry> packEntryList, final String displayName, final double buyPrice, final String currencyId, final String permission, final List<PackEntry> tradeCards) {
        this.id = id;
        this.packEntryList = packEntryList;
        this.displayName = displayName;
        this.buyPrice = buyPrice;
        this.currencyId = currencyId;
        this.permission = permission;
        this.tradeCards = tradeCards;
    }

    public String getDisplayName() {
        if(displayName == null)
            return id;
        return displayName;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public String getPermission() {
        return permission;
    }

    public List<PackEntry> getPackEntryList() {
        return packEntryList;
    }

    public List<PackEntry> getTradeCards() {
        return tradeCards;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(final String currencyId) {
        this.currencyId = currencyId;
    }

    public String getId() {
        return id;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public void setBuyPrice(final double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public void setPermission(final String permission) {
        this.permission = permission;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Pack) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.packEntryList, that.packEntryList) &&
                Objects.equals(this.displayName, that.displayName) &&
                Double.doubleToLongBits(this.buyPrice) == Double.doubleToLongBits(that.buyPrice) &&
                Objects.equals(this.permission, that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, packEntryList, displayName, buyPrice, permission);
    }

    @Override
    public String toString() {
        return "Pack[" +
                "id='" + id + '\'' +
                ", packEntryList=" + packEntryList +
                ", displayName='" + displayName + '\'' +
                ", buyPrice=" + buyPrice +
                ", currencyId='" + currencyId + '\'' +
                ", permission='" + permission + '\'' +
                ", tradeCards=" + tradeCards +
                ']';
    }


}