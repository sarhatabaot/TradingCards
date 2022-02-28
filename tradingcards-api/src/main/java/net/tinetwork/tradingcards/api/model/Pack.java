package net.tinetwork.tradingcards.api.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public final class Pack {
    private final String id;
    private final List<PackEntry> packEntryList;
    private String displayName;
    private double price;
    private String permissions;

    public Pack(String id, List<PackEntry> packEntryList,
                String displayName, double price, String permissions) {
        this.id = id;
        this.packEntryList = packEntryList;
        this.displayName = displayName;
        this.price = price;
        this.permissions = permissions;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getPrice() {
        return price;
    }

    public String getPermissions() {
        return permissions;
    }

    public List<PackEntry> getPackEntryList() {
        return packEntryList;
    }

    public String id() {
        return id;
    }

    public List<PackEntry> packEntryList() {
        return packEntryList;
    }

    public String displayName() {
        return displayName;
    }

    public double price() {
        return price;
    }

    public String permissions() {
        return permissions;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public void setPrice(final double price) {
        this.price = price;
    }

    public void setPermissions(final String permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Pack) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.packEntryList, that.packEntryList) &&
                Objects.equals(this.displayName, that.displayName) &&
                Double.doubleToLongBits(this.price) == Double.doubleToLongBits(that.price) &&
                Objects.equals(this.permissions, that.permissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, packEntryList, displayName, price, permissions);
    }

    @Override
    public String toString() {
        return "Pack[" +
                "id=" + id + ", " +
                "packEntryList=" + packEntryList + ", " +
                "displayName=" + displayName + ", " +
                "price=" + price + ", " +
                "permissions=" + permissions + ']';
    }


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

        @Override
        public String toString() {
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


}