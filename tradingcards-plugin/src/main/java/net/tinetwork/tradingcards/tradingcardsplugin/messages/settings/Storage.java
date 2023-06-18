package net.tinetwork.tradingcards.tradingcardsplugin.messages.settings;
public final class Storage {

	public static final String STORAGE_TYPE = "YAML";
	public static final Integer CONFIG_VERSION = 1;

	private Storage() {
		throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
	}

	public static final class Yaml {
		public static final String DEFAULT_FILE = "cards.yml";

		private Yaml() {
			throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
		}
	}

	public static final class Sql {
		public static final Boolean FIRST_TIME_VALUES = false;

		private Sql() {
			throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
		}
	}

	public static final class Database {
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

	public static final class DatabaseMigration {
		public static final String DEFAULT_SERIES_ID = "default";

		private DatabaseMigration() {
			throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
		}
	}
}