package net.tinetwork.tradingcards.api.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public final class Pack {
    private final String id;
    private final List<PackEntry> packEntryList;
    private String displayName;
    private double buyPrice;
    private String permission;

    public Pack(String id, List<PackEntry> packEntryList, String displayName, double buyPrice, String permission) {
        this.id = id;
        this.packEntryList = packEntryList;
        this.displayName = displayName;
        this.buyPrice = buyPrice;
        this.permission = permission;
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

    public String id() {
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

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "Pack[" +
                "id=" + id + ", " +
                "packEntryList=" + packEntryList + ", " +
                "displayName=" + displayName + ", " +
                "price=" + buyPrice + ", " +
                "permissions=" + permission + ']';
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


}