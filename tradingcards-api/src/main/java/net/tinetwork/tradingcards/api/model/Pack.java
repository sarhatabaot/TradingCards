package net.tinetwork.tradingcards.api.model;

import java.util.List;

public record Pack(List<PackEntry> packEntryList,
                   String displayName, String series, double price, String permissions) {

    public String getDisplayName() {
        return displayName;
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

    public record PackEntry(String rarityId, int amount) {

        public String getRarityId() {
            return rarityId;
        }

        public int getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return rarityId + ":" + amount;
        }

        public static PackEntry fromString(final String string) {
            final String[] split = string.split(":");
            final String rarityId = split[0];
            final int amount = Integer.parseInt(split[1]);
            return new PackEntry(rarityId, amount);
        }
    }


}