package net.tinetwork.tradingcards.tradingcardsplugin.messages.settings;

public final class Storage {
    public static final String STORAGE_TYPE = "YAML";

    public static class Database {
        public static final String ADDRESS = "localhost";
        public static final Integer PORT = 3306;
        public static final String DATABASE = "minecraft";
        public static final String USERNAME = "root";
        public static final String PASSWORD = "";
        public static final String TABLE_PREFIX = "tradingcards_";

        private Database() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static class DatabaseMigration {
        public static final String DEFAULT_SERIES_ID = "default";

        private DatabaseMigration() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    private Storage() {
        throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
    }
}