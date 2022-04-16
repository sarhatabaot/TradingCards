package net.tinetwork.tradingcards.tradingcardsplugin.messages;

public final class InternalMessages {
    public static final String STARTED_CONVERSION_FOR = "Started conversion for %s";
    public static final String VERSION = "The version is %version%";

    public static class Migrate {
        public static final String YAML_TO_YAML = "&4Cannot convert from YAML to YAML.";
        public static final String CHANGE_STORAGE_TYPE = "&4Please change your storage type to MYSQL or MARIADB & restart your server.";
        public static final String WARNING = "&cAre you sure you want to migrate? This action is irreversible.";
        public static final String BACKUP_HINT1 = "&cMake sure you have made a backup of your decks.yml before continuing.";
        public static final String BACKUP_HINT2 = "&cYou can easily backup all settings using /cards debug zip";
        public static final String CONFIRM_HINT = "&cIf you want to convert from YAML to ";
        public static final String CONFIRM_CMD = "&cPlease type /cards migrate <deck|data> confirm";

        private Migrate() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static class MigrateConfirm {
        public static final String START_MIGRATION = "&2Started migration for decks from YAML to %storage-type%";
        public static final String COMPLETE_MIGRATION = "&2Completed migration from YAML to %storage-type";
        public static final String WARNING = "&2This may take a while...";
        public static final String RESTART_HINT = "&cYou should restart your server, otherwise functionality of the plugin will be limited.";

        private MigrateConfirm() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    private InternalMessages() {
        throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
    }
}