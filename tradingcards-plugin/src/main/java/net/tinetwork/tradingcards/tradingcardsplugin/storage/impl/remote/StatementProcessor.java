package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote;

import org.jetbrains.annotations.NotNull;

public class StatementProcessor {
    private final String tablePrefix;

    public StatementProcessor(final String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public String applyPrefix(final @NotNull String statement) {
        return statement.replace("{prefix}", tablePrefix);
    }

}
