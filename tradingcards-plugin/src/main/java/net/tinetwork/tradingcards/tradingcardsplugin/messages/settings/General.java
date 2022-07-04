package net.tinetwork.tradingcards.tradingcardsplugin.messages.settings;

import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalExceptions;

public final class General {
    public static final Integer CONFIG_VERSION = 1;
    public static final Boolean DEBUG_MODE = false;
    public static final Boolean USE_DEFAULT_CARDS_FILE = true;

    public static class Colors {
        public static class Packs {
            public static final String BOOSTER_PACK_NAME = "&a";
            public static final String BOOSTER_PACK_LORE = "&7";
            public static final String BOOSTER_PACK_NORMAL_CARDS = "&e";

            private Packs() {
                throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
            }
        }

        public static class Lists {
            public static final String LIST_HAVE_CARD = "&a";
            public static final String LIST_HAVE_SHINY_CARD = "&e&l";
            public static final String LIST_RARITY_COMPLETE = "&e";

            private Lists() {
                throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
            }
        }

        private Colors() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static class Display {
        public static final String TITLE = "%prefix%%color%%name%";
        public static final String SHINY_TITLE = "%prefix%%color%%shiny_prefix% %name%";
        public static final String SERIES = "Series&f: ";
        public static final String TYPE = "Type&f: ";
        public static final String INFO = "Info&f: ";
        public static final String ABOUT = "About&f: ";

        private Display() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static final String CARD_MATERIAL = "PAPER";
    public static final String CARD_PREFIX = "&7[&fCard&7]&f ";
    public static final String SHINY_NAME = "Shiny";
    public static final String BOOSTER_PACK_MATERIAL = "BOOK";
    public static final String BOOSTER_PACK_PREFIX = "&7[&fPack&7]&f ";
    public static final Boolean DECKS_IN_CREATIVE = false;
    public static final Boolean USE_DECK_ITEM = true;
    public static final Boolean USE_LARGE_DECKS = false;
    public static final Integer DECK_ROWS = 6;
    public static final String DECK_MATERIAL = "BOOK";
    public static final String DECK_PREFIX = "&7[&fDeck&7]&f ";
    public static final Boolean DROP_DECK_ITEMS = true;
    public static final String PLAYER_OP_RARITY = "Legendary";
    public static final String PLAYER_SERIES = "player";
    public static final String PLAYER_TYPE = "Player";
    public static final Boolean PLAYER_HAS_SHINY_VERSION = true;
    public static final Boolean PLAYER_DROPS_CARD = true;
    public static final Integer PLAYER_DROPS_CARD_RARITY = 1000000;
    public static final Boolean ALLOW_REWARDS = true;
    public static final Boolean REWARD_BROADCAST = true;
    public static final Boolean EAT_SHINY_CARDS = false;

    public static class PluginSupport {
        public static class Vault {
            public static final Boolean VAULT_ENABLED = true;
            public static final Boolean CLOSED_ECONOMY = false;
            public static final String SERVER_ACCOUNT = "TradingCards-Bank";

            private Vault() {
                throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
            }
        }

        private PluginSupport() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static final Boolean SPAWNER_BLOCK = true;
    public static final Integer INFO_LINE_LENGTH = 25;

    private General() {
        throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
    }
}