package net.tinetwork.tradingcards.tradingcardsplugin.messages;

public final class InternalLog {
    public static class PluginStart {
        public static final String VAULT_HOOK_SUCCESS = "Vault hook successful!";
        public static final String VAULT_HOOK_FAIL = "Vault not found, hook unsuccessful!";
    }

    public static class Init {
        public static final String MANAGERS = "Initializing managers...";
        public static final String USING_STORAGE = "Using storage %s";
    }
}