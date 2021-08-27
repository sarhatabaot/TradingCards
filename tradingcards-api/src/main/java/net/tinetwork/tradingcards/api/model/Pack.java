package net.tinetwork.tradingcards.api.model;

import java.util.List;

public class Pack {
    private final List<PackEntry> packEntryList;
    private final String series;
    private final double price;
    private final String permissions;


    public Pack(List<PackEntry> packEntryList, String series, double price, String permissions) {
        this.packEntryList = packEntryList;
        this.series = series;
        this.price = price;
        this.permissions = permissions;
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

    public List<PackEntry> getPackEntryList() {
        return packEntryList;
    }

    public static class PackEntry {
        private final String rarityId;
        private final int amount;

        public PackEntry(String rarityId, int amount) {
            this.rarityId = rarityId;
            this.amount = amount;
        }

        public String getRarityId() {
            return rarityId;
        }

        public int getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return rarityId + ":"+amount;
        }

        public static PackEntry fromString(final String string) {
           final String[] split = string.split(":");
           final String rarityId = split[0];
           final int amount = Integer.parseInt(split[1]);
           return new PackEntry(rarityId,amount);
        }
    }


}