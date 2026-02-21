package net.tinetwork.tradingcards.tradingcardsplugin.messages.internal;
public final class Permissions {

	public static final String TOGGLE = "cards.toggle";

	private Permissions() {
		throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
	}

	public static final class User {
		public static final String USER = "cards.user";

		private User() {
			throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
		}

		public static final class Use {
			public static final String PACK = "cards.use.pack";
			public static final String DECK = "cards.use.deck";

			private Use() {
				throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
			}
		}

		public static final class List {
			public static final String LIST = "cards.list";
			public static final String LIST_PLAYER = "cards.list.player";
			public static final String LIST_PACK = "cards.list.pack";
			public static final String LIST_UPGRADE = "cards.list.upgrade";
			public static final String COLLECTOR = "cards.collector";

			private List() {
				throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
			}
		}

		public static final class Economy {
			public static final String WORTH = "cards.worth";
			public static final String SELL = "cards.sell";
			public static final String BUY = "cards.buy";
			public static final String BUY_PACK = "cards.buy.pack";
			public static final String BUY_CARD = "cards.buy.card";

			private Economy() {
				throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
			}
		}

		public static final class Info {
			public static final String INFO = "cards.info";
			public static final String INFO_CARD = "cards.info.card";
			public static final String INFO_RARITY = "cards.info.rarity";
			public static final String INFO_TYPE = "cards.info.type";
			public static final String INFO_SERIES = "cards.info.series";
			public static final String INFO_MOB = "cards.info.mob";
			public static final String INFO_PACK = "cards.info.pack";
			public static final String INFO_UPGRADE = "cards.info.upgrade";

			private Info() {
				throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
			}
		}
	}

	public static final class Admin {
		public static final String ADMIN = "cards.admin";
		public static final String RESOLVE = "cards.resolve";
		public static final String VERSION = "cards.version";
		public static final String RELOAD = "cards.reload";
		public static final String ADMIN_MIGRATE = "cards.admin.migrate";

		private Admin() {
			throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
		}

		public static final class Give {
			public static final String GIVE = "cards.give";
			public static final String GIVE_CARD = "cards.give.card";
			public static final String GIVE_CARD_SHINY = "cards.give.card.shiny";
			public static final String GIVE_CARD_PLAYER = "cards.give.card.player";
			public static final String GIVE_PACK = "cards.give.pack";
			public static final String GIVE_RANDOM_ENTITY = "cards.give.random.entity";
			public static final String GIVE_RANDOM_RARITY = "cards.give.random.rarity";

			private Give() {
				throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
			}
		}

		public static final class Debug {
			public static final String ADMIN_DEBUG_SHOW_CACHE = "cards.admin.debug.show_cache";
			public static final String ADMIN_DEBUG_MODULES = "cards.admin.debug.modules";
			public static final String ADMIN_DEBUG_PACKS = "cards.admin.debug.packs";
			public static final String ADMIN_DEBUG_RARITIES = "cards.admin.debug.rarities";
			public static final String ADMIN_DEBUG_RARITIES_SERIES = "cards.admin.debug.rarities.series";
			public static final String ADMIN_DEBUG_EXISTS = "cards.admin.debug.exists";
			public static final String ADMIN_DEBUG_ZIP = "cards.admin.debug.zip";
			public static final String ADMIN_DEBUG = "cards.admin.debug";

			private Debug() {
				throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
			}
		}

		public static final class Giveaway {
			public static final String GIVEAWAY_RARITY = "cards.giveaway.rarity";
			public static final String GIVEAWAY_ENTITY = "cards.giveaway.entity";

			private Giveaway() {
				throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
			}
		}

		public static final class Create {
			public static final String CREATE = "cards.create";
			public static final String CREATE_RARITY = "cards.create.rarity";
			public static final String CREATE_PACK = "cards.create.pack";
			public static final String CREATE_SERIES = "cards.create.series";
			public static final String CREATE_CARD = "cards.create.card";
			public static final String CREATE_CUSTOM_TYPE = "cards.create.customtype";
			public static final String CREATE_UPGRADE = "cards.create.upgrade";

			private Create() {
				throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
			}
		}

		public static final class Edit {
			public static final String EDIT = "cards.edit";
			public static final String EDIT_CARD = "cards.edit.card";
			public static final String EDIT_RARITY = "cards.edit.rarity";
			public static final String EDIT_SERIES = "cards.edit.series";
			public static final String EDIT_CUSTOM_TYPE = "cards.edit.customtype";
			public static final String EDIT_PACK = "cards.edit.pack";
			public static final String EDIT_UPGRADE = "cards.edit.upgrade";

			private Edit() {
				throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
			}
		}
	}
}
