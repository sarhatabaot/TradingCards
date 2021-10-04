package net.tinetwork.tradingcards.api.model;

import java.util.List;

public record Rarity(String name, String displayName, String defaultColor,
                     double buyPrice, double sellPrice, List<String> rewards) {

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultColor() {
        return defaultColor;
    }

    public List<String> getRewards() {
        return rewards;
    }


    @Override
    public String toString() {
        return "Rarity{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", defaultColor='" + defaultColor + '\'' +
                ", rewards=" + rewards +
                '}';
    }
}