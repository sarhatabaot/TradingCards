package net.tinetwork.tradingcards.tradingcardsplugin.messages.internal;

public final class InternalLog {
    public static class PluginStart {
        public static final String VAULT_HOOK_SUCCESS = "Vault hook successful!";
        public static final String VAULT_HOOK_FAIL = "Vault not found, hook unsuccessful!";

        private PluginStart() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static class Init {
        public static final String MANAGERS = "Initializing managers...";
        public static final String USING_STORAGE = "Using storage %s";
        public static final String LOAD_PACK_MANAGER = "Loaded PackManager.";
        public static final String LOAD_PACKS_AMOUNT = "Loaded %d packs.";
        public static final String LOAD_DROPTYPE_MANAGER = "Loaded DropTypeManager.";
        public static final String LOAD_SERIES_MANAGER = "Loaded SeriesManager.";
        public static final String LOAD_RARITY_MANAGER = "Loaded RarityManager.";
        public static final String LOAD_DECK_MANAGER = "Loaded DeckManager.";

        private Init() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static class CardManager {
        public static final String LOAD = "Loaded CardManager.";
        public static final String LOAD_CARDS = "Loaded %d cards.";
        public static final String LOAD_RARITIES = "Loaded %d rarities.";

        private CardManager() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static class DropType {
        public static final String COULD_NOT_GET_TYPE = "Could not get the type for %s reason: %s";
        public static final String DEFAULT = "Defaulting to passive.";

        private DropType() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static class Reload {
        public static final String ADDONS = "Reloaded %d addons.";
        public static final String SQL = "We have detected a reload. Shutting down connection to database and reconnecting..";

        private Reload() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    private InternalLog() {
        throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
    }
}