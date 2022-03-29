package net.tinetwork.tradingcards.api.model.schedule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Mode {
    ACTIVE,
    DISABLED,
    SCHEDULED;

    public static @Nullable Mode getMode(@NotNull String string) {
        try {
            return valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e){
            return null;
        }
    }
}