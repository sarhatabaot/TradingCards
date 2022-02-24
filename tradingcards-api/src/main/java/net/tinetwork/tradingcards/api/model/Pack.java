package net.tinetwork.tradingcards.api.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Pack(String id, List<PackEntry> packEntryList,
                   String displayName, double price, String permissions) {

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
            if(split.length > 2)
                seriesId = split[2];
            return new PackEntry(rarityId, amount, seriesId);
        }
    }


}