package net.tinetwork.tradingcards.api.model;

public class DropType {
    private final String id;
    private final String displayName;
    private final String type;

    public DropType(final String name, final String displayName, final String type) {
        this.id = name;
        this.displayName = displayName;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "DropType{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
