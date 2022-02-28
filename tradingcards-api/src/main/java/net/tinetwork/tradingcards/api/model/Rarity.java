package net.tinetwork.tradingcards.api.model;

import java.util.List;
import java.util.Objects;

public final class Rarity {
    private final String name;
    private String displayName;
    private String defaultColor;
    private double buyPrice;
    private double sellPrice;
    private List<String> rewards;

    public Rarity(String name, String displayName, String defaultColor,
                  double buyPrice, double sellPrice, List<String> rewards) {
        this.name = name;
        this.displayName = displayName;
        this.defaultColor = defaultColor;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.rewards = rewards;
    }

    public String getName() {
        return name;
    }

    public void setRewards(final List<String> rewards) {
        this.rewards = rewards;
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

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public void setDefaultColor(final String defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void setBuyPrice(final double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public void setSellPrice(final double sellPrice) {
        this.sellPrice = sellPrice;
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

    public String name() {
        return name;
    }

    public String displayName() {
        return displayName;
    }

    public String defaultColor() {
        return defaultColor;
    }

    public double buyPrice() {
        return buyPrice;
    }

    public double sellPrice() {
        return sellPrice;
    }

    public List<String> rewards() {
        return rewards;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Rarity) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.displayName, that.displayName) &&
                Objects.equals(this.defaultColor, that.defaultColor) &&
                Double.doubleToLongBits(this.buyPrice) == Double.doubleToLongBits(that.buyPrice) &&
                Double.doubleToLongBits(this.sellPrice) == Double.doubleToLongBits(that.sellPrice) &&
                Objects.equals(this.rewards, that.rewards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, displayName, defaultColor, buyPrice, sellPrice, rewards);
    }

}