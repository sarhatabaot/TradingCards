package net.tinetwork.tradingcards.api.model;

import java.util.List;

public class Rarity {
    private String name;
    private String displayName;
    private String defaultColor;
    private List<String> rewards;

    public Rarity(String name, String displayName, String defaultColor, List<String> rewards) {
        this.name = name;
        this.displayName = displayName;
        this.defaultColor = defaultColor;
        this.rewards = rewards;
    }

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
}