package net.tinetwork.tradingcards.tradingcardsplugin.drop;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum DropPoolEntryType {
    CARD("card_"),
    RARITY("rarity_");

    private final String configPrefix;

    DropPoolEntryType(final String configPrefix) {
        this.configPrefix = configPrefix;
    }

    public String configPrefix() {
        return configPrefix;
    }

    public static @Nullable ParsedDropPoolKey parseConfigKey(final @NotNull String rawKey) {
        final String lowerRawKey = rawKey.toLowerCase(Locale.ROOT);
        for (DropPoolEntryType value : values()) {
            if (!lowerRawKey.startsWith(value.configPrefix)) {
                continue;
            }

            final String id = rawKey.substring(value.configPrefix.length()).trim();
            if (id.isEmpty()) {
                return null;
            }

            return new ParsedDropPoolKey(value, id);
        }

        return null;
    }

    public record ParsedDropPoolKey(DropPoolEntryType type, String id) {
    }
}
