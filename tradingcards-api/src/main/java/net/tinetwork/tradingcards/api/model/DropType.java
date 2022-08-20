package net.tinetwork.tradingcards.api.model;

public class DropType {
    private final String id;
    private String displayName;
    private MobGroup mobGroup;

    public DropType(final String id, final String displayName, final MobGroup mobGroup) {
        this.id = id;
        this.displayName = displayName;
        this.mobGroup = mobGroup;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public MobGroup getMobGroup() {
        return mobGroup;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public void setMobGroup(final MobGroup mobGroup) {
        this.mobGroup = mobGroup;
    }

    @Override
    public String toString() {
        return "DropType{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", type='" + mobGroup + '\'' +
                '}';
    }
}
