package net.tinetwork.tradingcards.api.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public final class Rarity {
    private final String id;
    private String displayName;
    private String defaultColor;
    private double buyPrice;
    private double sellPrice;
    private List<String> rewards;

    public Rarity(String id, String displayName, String defaultColor,
                  double buyPrice, double sellPrice, List<String> rewards) {
        this.id = id;
        this.displayName = displayName;
        this.defaultColor = defaultColor;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.rewards = rewards;
    }

    public String getId() {
        return id;
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

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "Rarity{" +
                "name='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", defaultColor='" + defaultColor + '\'' +
                ", rewards=" + rewards +
                '}';
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Rarity) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.displayName, that.displayName) &&
                Objects.equals(this.defaultColor, that.defaultColor) &&
                Double.doubleToLongBits(this.buyPrice) == Double.doubleToLongBits(that.buyPrice) &&
                Double.doubleToLongBits(this.sellPrice) == Double.doubleToLongBits(that.sellPrice) &&
                Objects.equals(this.rewards, that.rewards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, displayName, defaultColor, buyPrice, sellPrice, rewards);
    }
}