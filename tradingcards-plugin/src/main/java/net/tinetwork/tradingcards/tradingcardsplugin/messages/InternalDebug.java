package net.tinetwork.tradingcards.tradingcardsplugin.messages;

public final class InternalDebug {
    public static final String BASE_DEBUG_FORMAT = "DEBUG %s %s";
    public static final String LOAD_PACK = "Loaded pack: %s";
    public static final String WHITELIST_MODE = "Whitelist Mode= %s";
    public static final String NO_RARITY = "No such rarity %s";
    public static final String NO_SERIES = "No such series %s";
    public static final String CARD_KEY = "CardKey= %s";

    public static class CardsManager {
        public static final String LOAD_RARITY_CARDS_IDS = "Loading Rarity & Series card ids:";
        public static final String RARITY_ID = "%s - Rarity Id";
        public static final String SERIES_ID = "%s - Series Id";
        public static final String RANDOM_CARD = "getRandomCard(),rarity= %s";
        public static final String EMPTY_ACTIVE_SERIES = "There are no cards in the active series. Not dropping anything.";
        public static final String DROP_CHANCE = "DropChance= %d AlwaysDrop= %s MobType= %s MobDropChance= %d";
        public static final String RARITY_CHANCE = "RarityChance= %d";
    }

    public static final String LOADED_INTO_CACHE = "Loaded into cache for %s";

    public static class DecksManager {
        public static final String HAS_MIGRATION = "Just ran migration? %s";
        public static final String PLAYER_UUID = "Deck UUID: %s";
        public static final String ADDED_DECK_UUID_NUMBER = "Added uuid %s deck #%d to deck viewer map.";
        public static final String REMOVED_DECK_UUID = "Removed uuid %s from deck viewer map.";
    }

    public static class DropListener {
        public static final String ENTITY_TYPE = "EntityType= %s";
        public static final String MOB_TYPE = "MobType= %s";
        public static final String ADDED_CARD = "Added card %s";
        public static final String NULL_RARITY_KEY = "rarityKey is null";
    }

    public static class DeckListener {
        public static final String ADDED_ENTRY = "Added %s to serialized list.";
        public static final String DECK_CLOSED = "Deck closed.";
    }

    public static class DeckEventListener {
        public static final String NO_ITEMS_OF_TYPE = "Doesn't contain any items of this type, ignoring.";
        public static final String NOT_A_PLAYER = "Not a player entity, ignoring.";
        public static final String NOT_OUR_GUI = "Not our gui, ignoring. UUID: %s";
        public static final String DECK_PLAYER = "deck: %d, player: %s, uuid: %s";
    }

    public static class Sql {
        public static final String COULD_NOT_FIND_DECK = "Could not find a deck for uuid= %s,decknumber= %d";
        public static final String UPDATE = "(UPDATE) %s";
        public static final String REMOVE = "(REMOVE) %s";
        public static final String EMPTY_RESULT = "Empty result for %s";
    }
}