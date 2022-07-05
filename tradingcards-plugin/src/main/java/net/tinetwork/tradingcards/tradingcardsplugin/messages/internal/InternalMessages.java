package net.tinetwork.tradingcards.tradingcardsplugin.messages.internal;

public final class InternalMessages {
    public static final String STARTED_CONVERSION_FOR = "Started conversion for %s";
    public static final String VERSION = "The version is %version%";

    public static class Migrate {
        public static final String YAML_TO_YAML = "&4Cannot convert from YAML to YAML.";
        public static final String CHANGE_STORAGE_TYPE = "&4Please change your storage type to MYSQL or MARIADB & restart your server.";
        public static final String WARNING = "&cAre you sure you want to migrate? This action is irreversible.";
        public static final String BACKUP_HINT1 = "&cMake sure you have made a backup of your decks.yml before continuing.";
        public static final String BACKUP_HINT2 = "&cYou can easily backup all settings using /cards debug zip";
        public static final String CONFIRM_HINT = "&cIf you want to convert from YAML to %s";
        public static final String CONFIRM_CMD = "&cPlease type /cards migrate <deck|data> confirm";

        private Migrate() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static class MigrateConfirm {
        public static final String START_MIGRATION = "&2Started migration for %s from YAML to %s";
        public static final String COMPLETE_MIGRATION = "&2Completed migration from YAML to %s";
        public static final String WARNING = "&2This may take a while...";
        public static final String RESTART_HINT = "&cYou should restart your server, otherwise functionality of the plugin will be limited.";

        private MigrateConfirm() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static class CardsCommand {
        public static final String VERSION = "%s %s API-%s";
        public static final String PLAYER_OFFLINE = "This player is not online. Or doesn't exist.";
        public static final String CANNOT_SELL = "Cannot sell this card.";

        private CardsCommand() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static class CreateCommand {
        public static final String CREATED_TYPE = "Created %s %s";
        public static final String CREATED_TYPE_EDIT = "To edit %s run /cards edit %s %s";
        public static final String SERIES_EXISTS = "Series %s already exists. Cannot create a new one.";
        public static final String TYPE_EXISTS = "Type %s already exists. Cannot create a new one.";
        public static final String RARITY_EXISTS = "Rarity %s already exists. Cannot create a new one.";
        public static final String CARD_EXISTS = "Card %s already exists. Cannot create a new one.";
        public static final String PACK_EXISTS = "Pack %s already exists. Cannot create a new one.";

        private CreateCommand() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static final String NO_RARITY = "Rarity &4%s&r doesn't exist.";
    public static final String NO_SERIES = "Series &4%s&r doesn't exist.";
    public static final String NO_CARD = "Card &4%s&r doesn't exist.";
    public static final String NO_TYPE = "Type &4%s&r doesn't exist.";
    public static final String NO_PACK = "Pack &4%s&r doesn't exist.";
    public static final String TYPE_MUST_BE = "Type must be: %s";

    public static class DebugCommand {
        public static final String ADDED_ALL_FILES = "Added all settings files to debug.zip.";
        public static final String ENABLED_MODULES = "Enabled Modules/Addons:";
        public static final String BACKING_UP_SETTING = "Backing the settings folder to debug.zip";
        public static final String BACKUP_HINT = "This does not backup storage.yml.";
        public static final String MODULES = "Modules:";
        public static final String ADDONS = "Addons:";

        private DebugCommand() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static class DeckCommand {
        public static final String CANNOT_RUN_FROM_CONSOLE = "Cannot run this command from console, or there was a problem getting the player object.";

        private DeckCommand() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static class EditCommand {
        public static final String PRICE_INCORRECT = "Price must be higher than -1";
        public static final String CUSTOM_MODEL_DATA_INCORRECT = "CustomModelData must be higher than 0.";
        public static final String NO_COLORS_ARGS = "Could not find any arguments for colors.";
        public static final String COLORS_HINT = "Must have: %s";
        public static final String MODE_INCORRECT = "Mode must be one of %s";
        public static final String CONTENTS_SYNTAX = "Incorrect syntax use: lineNumber=rarityId:cardId:amount:seriesId";
        public static final String CONTENTS_EXAMPLE = "For example: 0=common:zombie:1:default";
        public static final String LINE_NUMBER_INCORRECT = "Line number must be a number higher than -1";

        private EditCommand() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static class SellCommand {
        public static final String CANNOT_SELL_SHINY = "Cannot sell shiny card.";
        public static final String SOLD_CARD = "You have sold %dx%s for %.2f";

        private SellCommand() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static class InfoCommand {
        public static final String[] CARD_FORMAT = new String[] { "&bCard:&f %s", "&bSeries:&f %s", "&bRarity:&f %s",
                "&bDisplay Name:&f %s", "&bBuy Price:&f %.2f", "&bSell Price:&f %.2f", "&bCurrency:&f %s",
                "&bAbout:&f %s", "&bInfo:&f %s" };
        public static final String[] PACK_FORMAT = new String[] { "&bPack:&f %s", "&bDisplay Name:&f %s",
                "&bContent:&f %s", "&bCurrency:&f %s", "&bBuy Price:&f %s" };
        public static final String[] TYPE_FORMAT = new String[] { "&bType:&f %s", "&bDisplay Name:&f %s",
                "&bMob Type:&f %s" };
        public static final String[] SERIES_FORMAT = new String[] { "&bSeries:&f %s", "&bDisplay Name:&f '%s'",
                "&bMode:&f %s", "&bColors:&f %s" };
        public static final String[] RARITY_FORMAT = new String[] { "&bRarity:&f %s", "&bDisplay Name:&f '%s'",
                "&bDefault Color:&f %s", "&bBuy Price:&f %.2f", "&bSell Price:&f %.2f", "&bCurrency Id:&f %s",
                "&bRewards:&f %s" };
        public static final String MOB_FORMAT = "Entity %s is %s";

        private InfoCommand() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static final String CANNOT_HAVE_MORE_THAN_A_STACK = "Cannot have more than a stack of this card per deck.";

    private InternalMessages() {
        throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
    }
}