package net.tinetwork.tradingcards.api.model.schedule;

public enum Mode {
    ACTIVE,
    DISABLED,
    SCHEDULED;

    public static Mode getMode(String string) {
        return valueOf(string.toUpperCase());
    }
}