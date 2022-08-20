package net.tinetwork.tradingcards.api.model.chance;

import net.tinetwork.tradingcards.api.model.DropType;
import org.jetbrains.annotations.NotNull;

public class Chance {
    private final String id;
    private final int hostile;
    private final int neutral;
    private final int passive;
    private final int boss;

    public String getId() {
        return id;
    }

    public int getHostile() {
        return hostile;
    }

    public int getNeutral() {
        return neutral;
    }

    public int getPassive() {
        return passive;
    }

    public int getBoss() {
        return boss;
    }

    public Chance(String id, int hostile, int neutral, int passive, int boss) {
        this.id = id;
        this.hostile = hostile;
        this.neutral = neutral;
        this.passive = passive;
        this.boss = boss;
    }

    public int getFromMobType(final String mobType) {
        return switch (mobType) {
            case "HOSTILE" -> this.getHostile();
            case "NEUTRAL" -> this.getNeutral();
            case "PASSIVE" -> this.getPassive();
            case "BOSS" -> this.getBoss();
            default -> 0;
        };
    }

    public int getFromMobType(final @NotNull DropType dropType) {
        return switch (dropType.getMobGroup()) {
            case "hostile" -> this.getHostile();
            case "neutral" -> this.getNeutral();
            case "passive"-> this.getPassive();
            case "boss" -> this.getBoss();
            default -> 0;
        };
    }

}