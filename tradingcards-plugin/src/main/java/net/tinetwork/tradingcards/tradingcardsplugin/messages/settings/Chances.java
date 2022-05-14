package net.tinetwork.tradingcards.tradingcardsplugin.messages.settings;

import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalExceptions;

public final class Chances {
    public static final Integer HOSTILE_CHANCE = 20000;
    public static final Integer NEUTRAL_CHANCE = 5000;
    public static final Integer PASSIVE_CHANCE = 1000;
    public static final Integer BOSS_CHANCE = 100000;
    public static final Boolean BOSS_DROP = false;
    public static final Integer BOSS_DROP_RARITY = 5000;
    public static final Integer SHINY_VERSION_CHANCE = 1000;

    public static class Common {
        public static final Integer HOSTILE = 100000;
        public static final Integer NEUTRAL = 100000;
        public static final Integer PASSIVE = 100000;

        private Common() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static class Uncommon {
        public static final Integer HOSTILE = 20000;
        public static final Integer NEUTRAL = 10000;
        public static final Integer PASSIVE = 5000;

        private Uncommon() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static class Rare {
        public static final Integer HOSTILE = 1000;
        public static final Integer NEUTRAL = 500;

        private Rare() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static class VeryRare {
        public static final Integer HOSTILE = 10;
        public static final Integer BOSS = 100000;

        private VeryRare() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    public static class Legendary {
        public static final Integer HOSTILE = 1;
        public static final Integer BOSS = 50000;

        private Legendary() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    private Chances() {
        throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
    }
}