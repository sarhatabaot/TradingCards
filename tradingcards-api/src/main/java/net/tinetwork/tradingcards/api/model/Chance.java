package net.tinetwork.tradingcards.api.model;

public class Chance {
    private String id;
    private int hostile;
    private int neutral;
    private int passive;
    private int boss;

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

}