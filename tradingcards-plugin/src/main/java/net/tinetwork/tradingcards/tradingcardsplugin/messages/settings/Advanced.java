package net.tinetwork.tradingcards.tradingcardsplugin.messages.settings;
public final class Advanced {

	public static final Integer CONFIG_VERSION = "1";

	private Advanced() {
		throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
	}

	public static final class Cache {
		private Cache() {
			throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
		}

		public static final class Rarity {
			public static final Integer MAX_CACHE_ENTRIES = "1000";
			public static final Integer REFRESH_AFTER_WRITE = "5";

			private Rarity() {
				throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
			}
		}

		public static final class Series {
			public static final Integer MAX_CACHE_ENTRIES = "1000";
			public static final Integer REFRESH_AFTER_WRITE = "5";

			private Series() {
				throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
			}
		}

		public static final class Cards {
			public static final Integer MAX_CACHE_ENTRIES = "1000";
			public static final Integer REFRESH_AFTER_WRITE = "5";

			private Cards() {
				throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
			}
		}

		public static final class Types {
			public static final Integer MAX_CACHE_ENTRIES = "1000";
			public static final Integer REFRESH_AFTER_WRITE = "5";

			private Types() {
				throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
			}
		}

		public static final class Packs {
			public static final Integer MAX_CACHE_ENTRIES = "1000";
			public static final Integer REFRESH_AFTER_WRITE = "5";

			private Packs() {
				throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
			}
		}

		public static final class Upgrades {
			public static final Integer MAX_CACHE_ENTRIES = "1000";
			public static final Integer REFRESH_AFTER_WRITE = "5";

			private Upgrades() {
				throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
			}
		}
	}
}