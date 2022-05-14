package net.tinetwork.tradingcards.tradingcardsplugin.messages.settings;

public final class Advanced {
    public static class Cache {
        public static class Rarity {
            public static final Integer MAX_CACHE_ENTRIES = 100;
            public static final Integer REFRESH_AFTER_WRITE = 0;

            private Rarity() {
                throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
            }
        }

        public static class Series {
            public static final Integer MAX_CACHE_ENTRIES = 100;
            public static final Integer REFRESH_AFTER_WRITE = 0;

            private Series() {
                throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
            }
        }

        public static class Cards {
            public static final Integer MAX_CACHE_ENTRIES = 500;
            public static final Integer REFRESH_AFTER_WRITE = 0;

            private Cards() {
                throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
            }
        }

        public static class Types {
            public static final Integer MAX_CACHE_ENTRIES = 100;
            public static final Integer REFRESH_AFTER_WRITE = 0;

            private Types() {
                throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
            }
        }

        public static class Packs {
            public static final Integer MAX_CACHE_ENTRIES = 100;
            public static final Integer REFRESH_AFTER_WRITE = 0;

            private Packs() {
                throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
            }
        }

        private Cache() {
            throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
        }
    }

    private Advanced() {
        throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
    }
}