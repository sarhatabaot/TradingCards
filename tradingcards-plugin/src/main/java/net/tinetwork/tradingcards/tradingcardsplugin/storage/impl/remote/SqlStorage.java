package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote;

import com.google.common.collect.ImmutableMap;
import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.StorageType;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql.ConnectionFactory;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql.SchemaReader;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.io.InputStream;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * @author sarhatabaot
 */
public class SqlStorage implements Storage<TradingCard> {
    private static final String DECKS_SELECT_ALL_BY_UUID =
            "SELECT * FROM {prefix}decks " +
                    "WHERE uuid=?;";
    private static final String DECKS_SELECT_BY_DECK_NUMBER =
            "SELECT * FROM {prefix}decks " +
                    "WHERE uuid=? AND deck_number=?;";
    private static final String DECKS_SELECT_BY_CARD_AND_RARITY =
            "SELECT * FROM {prefix}decks " +
                    "WHERE uuid=? AND card_id=? AND rarity_id=?;";
    private static final String DECKS_SELECT_BY_CARD_AND_RARITY_SHINY =
            "SELECT * FROM {prefix}decks " +
                    "WHERE uuid=? AND card_id=? AND rarity_id=? AND is_shiny=true;";
    private static final String DECKS_SELECT_BY_CARD_AND_RARITY_AND_DECK =
            "SELECT * FROM {prefix}decks " +
                    "WHERE uuid=? AND card_id=? AND rarity_id=? AND deck_number=?;";
    private static final String DECKS_INSERT_CARD =
            "INSERT INTO {prefix}decks (uuid, deck_number, card_id, rarity_id, amount, is_shiny) " +
                    "VALUES (?,?,?,?,?,?);";
    private static final String DECKS_UPDATE_CARD =
            "UPDATE {prefix}decks " +
                    "SET uuid=?, deck_number=?, card_id=?, rarity_id=?, amount=?, is_shiny=? " +
                    "WHERE uuid=? AND deck_number=? AND card_id=? AND rarity_id=? AND is_shiny=?;";
    private static final String DECKS_REMOVE_CARD =
            "DELETE FROM {prefix}decks " +
                    "WHERE uuid=? AND deck_number=? AND card_id=? AND rarity_id=? AND is_shiny=?;";


    private static final String RARITY_SELECT_ALL =
            "SELECT * FROM {prefix}rarities;";
    private static final String RARITY_GET_BY_ID =
            "SELECT * FROM {prefix}rarities " +
                    "WHERE rarity_id=?;";

    private static final String RARITY_UPDATE_BUY_PRICE =
            "UPDATE {prefix}rarities " +
                    "SET buy_price=? " +
                    "WHERE rarity_id=?;";
    private static final String RARITY_UPDATE_SELL_PRICE =
            "UPDATE {prefix}rarities " +
                    "SET sell_price=? " +
                    "WHERE rarity_id=?;";
    private static final String RARITY_UPDATE_DEFAULT_COLOR =
            "UPDATE {prefix}rarities " +
                    "SET default_color=? " +
                    "WHERE rarity_id=?;";
    private static final String RARITY_UPDATE_DISPLAY_NAME =
            "UPDATE {prefix}rarities " +
                    "SET display_name=? " +
                    "WHERE rarity_id=?;";


    private static final String REWARDS_GET_BY_ID =
            "SELECT * FROM {prefix}rewards " +
                    "WHERE rarity_id=?" +
                    "ORDER BY command_order;";
    private static final String REWARDS_UPDATE_ADD_REWARD = //todo
            "UPDATE {prefix}rewards " +
                    "SET reward=? command_order=?" +
                    "WHERE rarity_id=?;";
    private static final String REWARDS_UPDATE_REMOVE_REWARD =
            "DELETE FROM {prefix}rewards " +
                    "WHERE rarity_id=? AND command_order=?;";
    private static final String REWARDS_UPDATE_REMOVE_ALL_REWARDS =
            "DELETE FROM {prefix}rewards " +
                    "WHERE rarity_id=?;";

    private static final String CARDS_SELECT_ALL =
            "SELECT * FROM {prefix}cards;";
    private static final String CARDS_SELECT_BY_CARD_ID_AND_RARITY =
            "SELECT * FROM {prefix}cards "+
                    "WHERE card_id=? AND rarity_id=?;";
    private static final String CARDS_SELECT_BY_RARITY_ID =
            "SELECT * FROM {prefix}cards " +
                    "WHERE rarity_id=?;";
    private static final String CARDS_SELECT_BY_SERIES_ID =
            "SELECT * FROM {prefix}cards " +
                    "WHERE series_id=?;";
    private static final String CARDS_SELECT_BY_RARITY_AND_SERIES =
            "SELECT * FROM {prefix}cards " +
                    "WHERE rarity_id=? AND series_id=?;";
    private static final String CARDS_CREATE =
            "INSERT INTO {prefix}cards (card_id,rarity_id,series_id) " +
                    "VALUES (?,?,?);";
    private static final String CARDS_UPDATE_DISPLAY_NAME =
            "UPDATE {prefix}cards " +
                    "SET display_name=? " +
                    "WHERE card_id=? AND rarity_id=? AND series_id=?;";
    private static final String CARDS_UPDATE_SERIES =
            "UPDATE {prefix}cards " +
                    "SET series_id=? " +
                    "WHERE card_id=? AND rarity_id=? AND series_id=?;";
    private static final String CARDS_UPDATE_SELL_PRICE =
            "UPDATE {prefix}cards " +
                    "SET sell_price=? " +
                    "WHERE card_id=? AND rarity_id=? AND series_id=?;";
    private static final String CARDS_UPDATE_BUY_PRICE =
            "UPDATE {prefix}cards " +
                    "SET buy_price=? " +
                    "WHERE card_id=? AND rarity_id=? AND series_id=?;";
    private static final String CARDS_UPDATE_TYPE =
            "UPDATE {prefix}cards " +
                    "SET type_id=? " +
                    "WHERE card_id=? AND rarity_id=? AND series_id=?;";
    private static final String CARDS_UPDATE_INFO =
            "UPDATE {prefix}cards " +
                    "SET info=? " +
                    "WHERE card_id=? AND rarity_id=? AND series_id=?;";
    private static final String CARDS_UPDATE_CUSTOM_MODEL_DATA =
            "UPDATE {prefix}cards " +
                    "SET custom_model_data=? " +
                    "WHERE card_id=? AND rarity_id=? AND series_id=?;";
    private static final String RARITY_CREATE =
            "INSERT INTO {prefix}rarities (rarity_id) " +
                    "VALUES (?);";
    private static final String SERIES_CREATE =
            "INSERT INTO {prefix}series (series_id) "+
                    "VALUES (?);";
    private static final String CUSTOM_TYPES_CREATE =
            "INSERT INTO {prefix}custom_types (type_id,drop_type) " +
                    "VALUES (?,?);";
    private static final String SERIES_SELECT_ALL =
            "SELECT * FROM {prefix}series;";

    private static final String SERIES_GET_BY_ID =
            "SELECT * FROM {prefix}series " +
                    "WHERE series_id=?;";

    private static final String SERIES_UPDATE_DISPLAY_NAME =
            "UPDATE {prefix}series " +
                    "SET display_name=? " +
                    "WHERE series_id=?;";
    private static final String SERIES_UPDATE_COLORS =
            "UPDATE {prefix}series_colors " +
                    "SET type=?, info=?, about=?, rarity=? " +
                    "WHERE series_id=?;";
    private static final String SERIES_UPDATE_MODE =
            "UPDATE {prefix}series " +
                    "SET mode=? " +
                    "WHERE series_id=?;";

    private static final String COLOR_GET_BY_ID =
            "SELECT * FROM {prefix}series_colors " +
                    "WHERE series_id=?;";

    private static final String SERIES_GET_BY_ID_ACTIVE =
            "SELECT * FROM {prefix}series " +
                    "WHERE series_id=? AND series_mode=ACTIVE;";

    private static final String PACKS_CREATE =
            "INSERT INTO {prefix}packs (pack_id) " +
                    "VALUES (?);";
    private static final String PACKS_SELECT_ALL =
            "SELECT * FROM {prefix}packs;";

    private static final String PACKS_GET_CONTENT_BY_ID =
            "SELECT * FROM {prefix}packs_content " +
                    "WHERE pack_id=?;";

    private static final String PACKS_GET_BY_ID =
            "SELECT * FROM {prefix}packs "+
                    "WHERE pack_id=?;";

    private static final String PACKS_UPDATE_DISPLAY_NAME =
            "UPDATE {prefix}packs " +
                    "SET display_name=? "+
                    "WHERE pack_id=?;";
    private static final String PACKS_UPDATE_PERMISSION =
            "UPDATE {prefix}packs " +
                    "SET permission=? "+
                    "WHERE pack_id=?;";
    private static final String PACKS_UPDATE_PRICE =
            "UPDATE {prefix}packs " +
                    "SET buy_price=? "+
                    "WHERE pack_id=?;";
    private static final String PACKS_UPDATE_CONTENT = //todo
            "UPDATE {prefix}packs_content " +
                    "SET rarity_id=?, card_amount=?, series_id=?" +
                    "WHERE pack_id=?;";

    private static final String CUSTOM_TYPES_SELECT_ALL =
            "SELECT * FROM {prefix}custom_types;";
    private static final String CUSTOM_TYPES_GET_BY_ID =
            "SELECT * FROM {prefix}custom_types " +
                    "WHERE type_id=?;";
    private static final String CUSTOM_TYPES_UPDATE_TYPE =
            "UPDATE {prefix}custom_types " +
                    "SET drop_type=?" +
                    "WHERE type_id=?;";
    private static final String CUSTOM_TYPES_UPDATE_DISPLAY_NAME =
            "UPDATE {prefix}custom_types " +
                    "SET display_name=?" +
                    "WHERE type_id=?;";


    private static final String COLUMN_UUID = "uuid";
    private static final String COLUMN_CARD_ID = "card_id";
    private static final String COLUMN_RARITY_ID = "rarity_id";
    private static final String COLUMN_DECK_NUMBER = "deck_number";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_IS_SHINY = "is_shiny";

    private static final String COLUMN_DEFAULT_COLOR = "default_color";
    private static final String COLUMN_DISPLAY_NAME = "display_name";
    private static final String COLUMN_BUY_PRICE = "buy_price";
    private static final String COLUMN_SELL_PRICE = "sell_price";
    private static final String COLUMN_HAS_SHINY = "has_shiny";
    private static final String COLUMN_INFO = "info";
    private static final String COLUMN_CUSTOM_MODEL_DATA = "custom_model_data";

    private static final String COLUMN_SERIES_ID = "series_id";
    private static final String COLUMN_MODE = "mode";

    private static final String COLUMN_PACK_ID = "pack_id";
    private static final String COLUMN_PACK_PERMISSION = "permission";

    private static final String COLUMN_COMMAND = "command";
    private static final String COLUMN_ORDER_NUMBER = "command_order";
    private static final String COLUMN_CARD_AMOUNT = "card_amount";

    private static final String COLUMN_TYPE_ID = "type_id";
    private static final String COLUMN_DROP_TYPE = "drop_type";

    private static final String COLUMN_COLOR_ABOUT = "about";
    private static final String COLUMN_COLOR_INFO = "info";
    private static final String COLUMN_COLOR_TYPE = "type";
    private static final String COLUMN_COLOR_RARITY= "rarity";
    private static final String COLUMN_COLOR_SERIES = "series";

    private final TradingCards plugin;
    private final ConnectionFactory connectionFactory;
    private final StatementProcessor statementProcessor;


    @Override
    public void init(final TradingCards plugin) {
        connectionFactory.init(plugin);
        try {
            applySchema();
        } catch (SQLException | IOException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    public SqlStorage(final TradingCards plugin, final String tablePrefix, final ConnectionFactory connectionFactory) {
        this.plugin = plugin;
        this.connectionFactory = connectionFactory;
        this.statementProcessor = new StatementProcessor(tablePrefix, plugin);
    }

    @Override
    public List<Deck> getPlayerDecks(final UUID playerUuid) {
        return new ExecuteQuery<List<Deck>>() {
            @Override
            public List<Deck> getQuery(final ResultSet resultSet) throws SQLException {
                List<Deck> decks = new ArrayList<>();
                while (resultSet.next()) {
                    decks.add(getDeckFromResultSet(resultSet));
                }
                return decks;
            }

            @Override
            public List<Deck> returnNull() {
                return Collections.emptyList();
            }
        }.runQuery(DECKS_SELECT_ALL_BY_UUID, null, Map.of(COLUMN_UUID, statementProcessor.wrap(playerUuid.toString())));
    }

    @Override
    public Deck getDeck(final UUID playerUuid, final int deckNumber) {
        return new ExecuteQuery<Deck>() {
            @Override
            public Deck getQuery(final ResultSet resultSet) throws SQLException {
                if (resultSet.next()) {
                    return getDeckFromResultSet(resultSet);
                }
                if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                    plugin.debug(getClass(), "Could not find a deck for uuid=" + playerUuid + ",decknumber=" + deckNumber);
                    return new Deck(playerUuid, deckNumber, new ArrayList<>());
                }
                return null;
            }

            @Override
            public Deck returnNull() {
                return null;
            }
        }.runQuery(DECKS_SELECT_BY_DECK_NUMBER, null,
                Map.of(COLUMN_UUID, statementProcessor.wrap(playerUuid.toString()),
                        COLUMN_DECK_NUMBER, String.valueOf(deckNumber)));
    }

    @Contract("_ -> new")
    private @NotNull Deck getDeckFromResultSet(@NotNull ResultSet resultSet) throws SQLException {
        final String playerUuid = statementProcessor.unwrap(resultSet.getString(COLUMN_UUID));
        final int deckNumber = resultSet.getInt(COLUMN_DECK_NUMBER);
        List<StorageEntry> entries = new ArrayList<>();
        do {
            final String rarityId = resultSet.getString(COLUMN_RARITY_ID);
            final String cardId = resultSet.getString(COLUMN_CARD_ID);
            final boolean isShiny = resultSet.getBoolean(COLUMN_IS_SHINY);
            final int amount = resultSet.getInt(COLUMN_AMOUNT);
            entries.add(new StorageEntry(rarityId, cardId, amount, isShiny));
        } while (resultSet.next());
        return new Deck(UUID.fromString(playerUuid), deckNumber, entries);
    }

    //Implements a simple comparator to allow for sorting
    //Entries will be sorted by rarityid and then by card name
    public static class StorageEntryComparator implements Comparator<StorageEntry> {
        @Override
        public int compare(final @NotNull StorageEntry o1, final StorageEntry o2) {
            if (o1.equals(o2))
                return 0;
            if (o1.getCardId().equals(o2.getCardId()))
                return o1.getRarityId().compareTo(o2.getRarityId());
            return o1.getCardId().compareTo(o2.getCardId());
        }
    }

    @Override
    public @Nullable Rarity getRarityById(final String rarityId) {
        return new ExecuteQuery<Rarity>() {
            @Override
            public Rarity getQuery(final ResultSet resultSet) throws SQLException {
                if (resultSet.next()) {
                    final String displayName = resultSet.getString(COLUMN_DISPLAY_NAME);
                    final String defaultColor = resultSet.getString(COLUMN_DEFAULT_COLOR);
                    final List<String> rewards = getRewards(rarityId);
                    final double buyPrice = resultSet.getDouble(COLUMN_BUY_PRICE);
                    final double sellPrice = resultSet.getDouble(COLUMN_SELL_PRICE);
                    return new Rarity(rarityId, displayName, defaultColor, buyPrice, sellPrice, rewards);
                }
                if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                    plugin.getLogger().info("No such rarity " + rarityId);
                }
                return returnNull();
            }

            @Override
            public Rarity returnNull() {
                return null;
            }
        }.runQuery(RARITY_GET_BY_ID, null, Map.of(COLUMN_RARITY_ID, rarityId));
    }

    @Override
    public List<String> getRewards(final String rarityId) {
        return new ExecuteQuery<List<String>>() {
            @Override
            public List<String> getQuery(final ResultSet resultSet) throws SQLException {
                List<String> rewards = new ArrayList<>();
                while (resultSet.next()) {
                    final String command = resultSet.getString(COLUMN_COMMAND);
                    rewards.add(command);
                }

                if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                    plugin.getLogger().info("No such rarity " + rarityId);
                    return returnNull();
                }

                return rewards;
            }

            @Override
            public List<String> returnNull() {
                return Collections.emptyList();
            }
        }.runQuery(REWARDS_GET_BY_ID, null, Map.of(COLUMN_RARITY_ID, rarityId));
    }

    @Override
    public Series getSeries(final String seriesId) {
        return new ExecuteQuery<Series>() {
            @Override
            public Series getQuery(final ResultSet resultSet) throws SQLException {
                if (resultSet.next()) {
                    final String id = resultSet.getString(COLUMN_SERIES_ID);
                    final String displayName = resultSet.getString(COLUMN_DISPLAY_NAME);
                    final Mode mode = Mode.getMode(resultSet.getString(COLUMN_MODE));
                    final ColorSeries colorSeries = getColorSeries(id);
                    return new Series(id, mode, displayName, null, colorSeries);
                }
                if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                    plugin.getLogger().info("No such series " + seriesId);

                }
                return null;
            }

            @Override
            public Series returnNull() {
                return null;
            }
        }.runQuery(SERIES_GET_BY_ID, null, Map.of(COLUMN_SERIES_ID, seriesId));
    }

    @Override
    public void saveDeck(final UUID playerUuid, final int deckNumber, final @NotNull Deck deck) {
        //Get current Deck. Compare which cards don't exist anymore.
        //Get a list of cards to remove, if any exist.
        Deck dbDeck = getDeck(playerUuid, deckNumber);
        if (deck.equals(dbDeck)) {
            //It's the same! Don't make any changes
            return;
        }

        //Compare Decks
        List<StorageEntry> deckEntries = deck.getDeckEntries();
        List<StorageEntry> dbDeckEntries = dbDeck.getDeckEntries();

        deckEntries.sort(new StorageEntryComparator());
        dbDeckEntries.sort(new StorageEntryComparator());

        List<StorageEntry> cardsToUpdate = new ArrayList<>();
        List<StorageEntry> cardsToAdd = new ArrayList<>();
        List<StorageEntry> cardsToRemove = new ArrayList<>();

        for (StorageEntry deckEntry : deckEntries) {
            boolean cardExistsInDatabase = dbDeck.containsCard(deckEntry.getCardId(), deckEntry.getRarityId(), deckEntry.isShiny());
            //if it exists, but the entry is 64, add a new line?
            if (cardExistsInDatabase) {
                if (!dbDeckEntries.contains(deckEntry)) {
                    cardsToUpdate.add(deckEntry);
                }
                //If it contains exactly the same entry, do nothing to it.
            } else {
                //It doesn't exist, add it!
                cardsToAdd.add(deckEntry);
            }
        }

        for (StorageEntry dbDeckEntry : dbDeckEntries) {
            boolean cardExistsInDeck = deck.containsCard(dbDeckEntry.getCardId(), dbDeckEntry.getRarityId(), dbDeckEntry.isShiny());
            if (!cardExistsInDeck) {
                cardsToRemove.add(dbDeckEntry);
            }
        }

        //UpdateValues
        if (!cardsToUpdate.isEmpty()) {
            for (StorageEntry entryToUpdate : cardsToUpdate) {
                updateCard(playerUuid, deckNumber, entryToUpdate);
            }
        }
        if (!cardsToAdd.isEmpty()) {
            for (StorageEntry entryToAdd : cardsToAdd) {
                addCardToDeck(playerUuid, deckNumber, entryToAdd);
            }
        }
        if (!cardsToRemove.isEmpty()) {
            for (StorageEntry entryToRemove : cardsToRemove) {
                removeCardFromDeck(playerUuid, deckNumber, entryToRemove);
            }
        }
    }


    private void updateCard(final @NotNull UUID playerUuid, final int deckNumber, final @NotNull StorageEntry storageEntry) {
        try (Connection connection = connectionFactory.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(DECKS_UPDATE_CARD,
                    null,
                    Map.of(COLUMN_UUID, statementProcessor.wrap(playerUuid.toString()),
                            COLUMN_DECK_NUMBER, String.valueOf(deckNumber),
                            COLUMN_CARD_ID, statementProcessor.wrap(storageEntry.getCardId()),
                            COLUMN_RARITY_ID, statementProcessor.wrap(storageEntry.getRarityId()),
                            COLUMN_IS_SHINY, String.valueOf(storageEntry.isShiny())),
                    Map.of(COLUMN_UUID, statementProcessor.wrap(playerUuid.toString()),
                            COLUMN_DECK_NUMBER, String.valueOf(deckNumber),
                            COLUMN_CARD_ID, statementProcessor.wrap(storageEntry.getCardId()),
                            COLUMN_RARITY_ID, statementProcessor.wrap(storageEntry.getRarityId()),
                            COLUMN_AMOUNT, String.valueOf(storageEntry.getAmount()),
                            COLUMN_IS_SHINY, String.valueOf(storageEntry.isShiny()))))) {
                statement.executeUpdate();
                plugin.debug(SqlStorage.class, "(UPDATE) " + storageEntry);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    @Override
    public boolean hasCard(final UUID playerUuid, final String card, final String rarity) {
        try (Connection connection = connectionFactory.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(DECKS_SELECT_BY_CARD_AND_RARITY, null,
                    Map.of(COLUMN_UUID, statementProcessor.wrap(playerUuid.toString()),
                            COLUMN_CARD_ID, statementProcessor.wrap(card),
                            COLUMN_RARITY_ID, statementProcessor.wrap(rarity))))) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    //try and access a result, if it doesn't exist, return false.
                    if (resultSet.next()) {
                        String cardId = resultSet.getString(COLUMN_CARD_ID);
                        String rarityId = resultSet.getString(COLUMN_RARITY_ID);
                        plugin.debug(SqlStorage.class, cardId);
                        return card.equals(cardId) && rarity.equals(rarityId);
                    }
                    if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    @Override
    public boolean hasShinyCard(final UUID playerUuid, final String card, final String rarity) {
        try (Connection connection = connectionFactory.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(DECKS_SELECT_BY_CARD_AND_RARITY_SHINY, null,
                    Map.of(COLUMN_UUID, statementProcessor.wrap(playerUuid.toString()),
                            COLUMN_CARD_ID, statementProcessor.wrap(card),
                            COLUMN_RARITY_ID, statementProcessor.wrap(rarity),
                            COLUMN_IS_SHINY, String.valueOf(true))))) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        //try and access a result, if it doesn't exist, return false.
                        String cardId = resultSet.getString(COLUMN_CARD_ID); //todo
                        plugin.debug(SqlStorage.class, cardId);
                        return !resultSet.wasNull();
                    }
                    if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    @Override
    public StorageType getType() {
        return StorageType.MYSQL;
    }


    public void addCardToDeck(final UUID playerUuid, final int deckNumber, final @NotNull StorageEntry entry) {
        final String cardId = entry.getCardId();
        final String rarityId = entry.getRarityId();
        final int amount = entry.getAmount();
        final boolean isShiny = entry.isShiny();
        ImmutableMap<String, String> values = statementProcessor.generateValuesMap(playerUuid, deckNumber, cardId, rarityId, amount, isShiny);
        try (Connection connection = connectionFactory.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(DECKS_INSERT_CARD, values, null))) {
                statement.executeUpdate();
                plugin.debug(SqlStorage.class, "(ADD) " + entry);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    public void removeCardFromDeck(final UUID playerUuid, final int deckNumber, final @NotNull StorageEntry entry) {
        try (Connection connection = connectionFactory.getConnection()) {
            ImmutableMap<String, String> values = statementProcessor.generateValuesMap(playerUuid, deckNumber, entry.getCardId(), entry.getRarityId(), entry.getAmount(), entry.isShiny());
            try (PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(DECKS_REMOVE_CARD, values,
                    Map.of(COLUMN_UUID, statementProcessor.wrap(playerUuid.toString()),
                            COLUMN_DECK_NUMBER, String.valueOf(deckNumber),
                            COLUMN_CARD_ID, statementProcessor.wrap(entry.getCardId()),
                            COLUMN_RARITY_ID, statementProcessor.wrap(entry.getRarityId()),
                            COLUMN_AMOUNT, String.valueOf(entry.getAmount()),
                            COLUMN_IS_SHINY, String.valueOf(entry.isShiny()))))) {
                statement.execute();
                plugin.debug(SqlStorage.class, "(REMOVE) " + entry);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    //From LuckPerms.
    private void applySchema() throws IOException, SQLException {
        List<String> statements;

        String schemaFileName = "schema/" + this.connectionFactory.getType().toLowerCase(Locale.ROOT) + ".sql";
        try (InputStream is = this.plugin.getResource(schemaFileName)) {
            if (is == null) {
                throw new IOException("Couldn't locate schema file for " + this.connectionFactory.getType());
            }

            statements = SchemaReader.getStatements(is).stream()
                    .map(this.statementProcessor::applyPrefix)
                    .toList();
        }

        statements.forEach(s -> plugin.debug(SqlStorage.class, s));

        try (Connection connection = this.connectionFactory.getConnection()) {
            boolean utf8mb4Unsupported = false;

            try (Statement s = connection.createStatement()) {
                for (String query : statements) {
                    s.addBatch(query);
                }

                try {
                    s.executeBatch();
                } catch (BatchUpdateException e) {
                    if (e.getMessage().contains("Unknown character set")) {
                        utf8mb4Unsupported = true;
                    } else {
                        throw e;
                    }
                }
            }

            // try again
            if (utf8mb4Unsupported) {
                try (Statement s = connection.createStatement()) {
                    for (String query : statements) {
                        s.addBatch(query.replace("utf8mb4", "utf8"));
                    }

                    s.executeBatch();
                }
            }
        }
    }

    @Override
    public List<Rarity> getRarities() {
        return new ExecuteQuery<List<Rarity>>() {
            @Override
            public List<Rarity> getQuery(final ResultSet resultSet) throws SQLException {
                final List<Rarity> rarities = new ArrayList<>();
                while (resultSet.next()) {
                    final String id = resultSet.getString(COLUMN_RARITY_ID);
                    final String displayName = resultSet.getString(COLUMN_DISPLAY_NAME);
                    final String defaultColor = resultSet.getString(COLUMN_DEFAULT_COLOR);
                    final double buyPrice = resultSet.getDouble(COLUMN_BUY_PRICE);
                    final double sellPrice = resultSet.getDouble(COLUMN_SELL_PRICE);
                    final List<String> rewards = getRewards(id);
                    rarities.add(new Rarity(id, displayName, defaultColor, buyPrice, sellPrice, rewards));
                }
                if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                    return returnNull();
                }
                return rarities;
            }

            @Override
            public List<Rarity> returnNull() {
                return Collections.emptyList();
            }
        }.runQuery(RARITY_SELECT_ALL, null, null);
    }


    @Override
    public Collection<Series> getAllSeries() {
        return new ExecuteQuery<Collection<Series>>() {
            @Override
            public Collection<Series> getQuery(final ResultSet resultSet) throws SQLException {
                List<Series> series = new ArrayList<>();
                while (resultSet.next()) {
                    series.add(getSeriesFromResult(resultSet));
                }
                if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                    return returnNull();
                }
                return series;
            }

            @Override
            public Collection<Series> returnNull() {
                return Collections.emptyList();
            }
        }.runQuery(SERIES_SELECT_ALL, null, null);
    }

    @Contract("_ -> new")
    private @NotNull ColorSeries getColorSeries(final String seriesId) {
        return new ExecuteQuery<ColorSeries>() {
            @Override
            public ColorSeries getQuery(final ResultSet resultSet) throws SQLException {
                if (resultSet.next()) {
                    final String about = resultSet.getString(COLUMN_COLOR_ABOUT);
                    final String info = resultSet.getString(COLUMN_COLOR_INFO);
                    final String type = resultSet.getString(COLUMN_COLOR_TYPE);
                    final String rarity = resultSet.getString(COLUMN_COLOR_RARITY);
                    final String series = resultSet.getString(COLUMN_COLOR_SERIES);
                    return new ColorSeries(series, type, info, about, rarity);
                }

                return returnNull();
            }

            @Override
            public ColorSeries returnNull() {
                return Util.DEFAULT_COLORS;
            }
        }.runQuery(COLOR_GET_BY_ID, null, Map.of(COLUMN_SERIES_ID, seriesId));
    }

    @Override
    public Set<Series> getActiveSeries() {
        return new ExecuteQuery<Set<Series>>() {
            @Override
            public Set<Series> getQuery(final ResultSet resultSet) throws SQLException {
                final Set<Series> activeSeries = new HashSet<>();
                while (resultSet.next()) {
                    final Series series = getSeriesFromResult(resultSet);
                    activeSeries.add(series);
                }

                if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                    return Collections.emptySet();
                }
                return activeSeries;
            }

            @Override
            public Set<Series> returnNull() {
                return Collections.emptySet();
            }
        }.runQuery(SERIES_GET_BY_ID_ACTIVE, null, null);
    }

    @Override
    public Map<String, TradingCard> getCardsMap() {
        Map<String, TradingCard> cardsMap =  new HashMap<>();
        for(TradingCard tradingCard: getCards()) {
            final String cardId = tradingCard.getCardName();
            final String rarityId = tradingCard.getRarity().getName();
            final String cardKey = CardUtil.cardKey(rarityId,cardId);
            cardsMap.put(cardKey,tradingCard);
        }
        return cardsMap;
    }

    @Override
    public Map<String, TradingCard> getActiveCardsMap() {
        return null;
    }

    @Override
    public List<TradingCard> getCards() {
        return new ExecuteQuery<List<TradingCard>>() {
            @Override
            public List<TradingCard> getQuery(final ResultSet resultSet) throws SQLException {
                List<TradingCard> cards = new ArrayList<>();
                while (resultSet.next()) {
                    final TradingCard card = getTradingCardFromResult(resultSet);
                    cards.add(card);
                }
                if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                    return Collections.emptyList();
                }
                return cards;
            }

            @Override
            public List<TradingCard> returnNull() {
                return Collections.emptyList();
            }
        }.runQuery(CARDS_SELECT_ALL,null,null);
    }

    private @NotNull Series getSeriesFromResult(final @NotNull ResultSet resultSet) throws SQLException {
        final String id = resultSet.getString(COLUMN_SERIES_ID);
        final String displayName = resultSet.getString(COLUMN_DISPLAY_NAME);
        final Mode mode = Mode.getMode(resultSet.getString(COLUMN_MODE));
        final ColorSeries colorSeries = getColorSeries(id);
        return new Series(id, mode, displayName, null, colorSeries);
    }

    private @NotNull TradingCard getTradingCardFromResult(final @NotNull ResultSet resultSet) throws SQLException {
        final String id = resultSet.getString(COLUMN_CARD_ID);
        final String displayName = resultSet.getString(COLUMN_DISPLAY_NAME);
        final Rarity rarity = getRarityById(resultSet.getString(COLUMN_RARITY_ID));
        final boolean hasShiny = resultSet.getBoolean(COLUMN_HAS_SHINY);
        final Series series = getSeries(resultSet.getString(COLUMN_SERIES_ID));
        final String info = resultSet.getString(COLUMN_INFO);
        final int customModelData = resultSet.getInt(COLUMN_CUSTOM_MODEL_DATA);
        final double buyPrice = resultSet.getDouble(COLUMN_BUY_PRICE);
        final double sellPrice = resultSet.getDouble(COLUMN_SELL_PRICE);
        final TradingCard card = new TradingCard(id);
        card.displayName(displayName)
                .rarity(rarity)
                .hasShiny(hasShiny)
                .series(series)
                .info(info)
                .customModelNbt(customModelData)
                .buyPrice(buyPrice)
                .sellPrice(sellPrice);
        return card;
    }

    @Override
    public List<TradingCard> getCardsInRarity(final String rarityId) {
        return new ExecuteQuery<List<TradingCard>>() {
            @Override
            public List<TradingCard> getQuery(final ResultSet resultSet) throws SQLException {
                List<TradingCard> cards = new ArrayList<>();
                while (resultSet.next()) {
                    final TradingCard card = getTradingCardFromResult(resultSet);
                    cards.add(card);
                }
                if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                    return Collections.emptyList();
                }
                return cards;
            }

            @Override
            public List<TradingCard> returnNull() {
                return Collections.emptyList();
            }
        }.runQuery(CARDS_SELECT_BY_RARITY_ID, null, Map.of(COLUMN_RARITY_ID, rarityId));
    }

    @Override
    public List<TradingCard> getCardsInSeries(final String seriesId) {
        return new ExecuteQuery<List<TradingCard>>() {
            @Override
            public List<TradingCard> getQuery(final ResultSet resultSet) throws SQLException {
                List<TradingCard> cards = new ArrayList<>();
                while (resultSet.next()) {
                    final TradingCard card = getTradingCardFromResult(resultSet);
                    cards.add(card);
                }
                if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                    return returnNull();
                }
                return cards;
            }

            @Override
            public List<TradingCard> returnNull() {
                return Collections.emptyList();
            }
        }.runQuery(CARDS_SELECT_BY_SERIES_ID, null, Map.of(COLUMN_SERIES_ID, seriesId));
    }

    @Override
    public List<TradingCard> getCardsInRarityAndSeries(final String rarityId, final String seriesId) {
        return new ExecuteQuery<List<TradingCard>>() {
            @Override
            public List<TradingCard> getQuery(final ResultSet resultSet) throws SQLException {
                List<TradingCard> cards = new ArrayList<>();
                while(resultSet.next()) {
                    cards.add(getTradingCardFromResult(resultSet));
                }
                if(resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                    return returnNull();
                }
                return cards;
            }

            @Override
            public List<TradingCard> returnNull() {
                return Collections.emptyList();
            }
        }.runQuery(CARDS_SELECT_BY_RARITY_AND_SERIES,null,Map.of(COLUMN_RARITY_ID,rarityId,COLUMN_SERIES_ID,seriesId));
    }

    @Override
    public List<TradingCard> getActiveCards() {
        List<TradingCard> activeCards = new ArrayList<>();
        for(Series series: getActiveSeries()) {
            activeCards = Stream.concat(activeCards.stream(),getCardsInSeries(series.getName()).stream()).toList();
        }
        return activeCards;
    }

    @Override
    public Card<TradingCard> getCard(final String cardId, final String rarityId) {
        return new ExecuteQuery<TradingCard>() {
            @Override
            public TradingCard getQuery(final ResultSet resultSet) throws SQLException {
                if (resultSet.next()) {
                    return getTradingCardFromResult(resultSet);
                }
                return returnNull();
            }

            @Override
            public TradingCard returnNull() {
                return new EmptyCard();
            }
        }.runQuery(CARDS_SELECT_BY_CARD_ID_AND_RARITY, null, Map.of(COLUMN_RARITY_ID, rarityId, COLUMN_CARD_ID, cardId));
    }

    @Override
    public Card<TradingCard> getActiveCard(final String cardId, final String rarityId) {
        //TODO
        return null;
    }

    @Override
    public @Nullable Pack getPack(final String packsId) {
        return new ExecuteQuery<Pack>() {
            @Override
            public Pack getQuery(final ResultSet resultSet) throws SQLException {
                if(resultSet.next()){
                    return getPackFromResult(resultSet);
                }
                return returnNull();
            }

            @Override
            public Pack returnNull() {
                return null;
            }
        }.runQuery(PACKS_GET_BY_ID,null,Map.of(COLUMN_PACK_ID, packsId));
    }


    private @NotNull Pack getPackFromResult(@NotNull ResultSet resultSet) throws SQLException{
        final String id = resultSet.getString(COLUMN_PACK_ID);
        final String displayName = resultSet.getString(COLUMN_DISPLAY_NAME);
        final double price = resultSet.getDouble(COLUMN_BUY_PRICE);
        final String permission =resultSet.getString(COLUMN_PACK_PERMISSION);
        final List<Pack.PackEntry> entries = getPackEntries(id);
        return new Pack(id,entries,displayName,price,permission);
    }
    private Pack.@NotNull PackEntry getPackEntryFromResult(@NotNull ResultSet resultSet) throws SQLException{
        final String rarityId = resultSet.getString(COLUMN_RARITY_ID);
        final String seriesId = resultSet.getString(COLUMN_SERIES_ID);
        final int cardAmount = resultSet.getInt(COLUMN_CARD_AMOUNT);
        return new Pack.PackEntry(rarityId,cardAmount,seriesId);
    }
    private @NotNull @Unmodifiable List<Pack.PackEntry> getPackEntries(final String packId) {
        return new ExecuteQuery<List<Pack.PackEntry>>() {
            @Override
            public List<Pack.PackEntry> getQuery(final ResultSet resultSet) throws SQLException {
                List<Pack.PackEntry> entries = new ArrayList<>();
                while(resultSet.next()) {
                    int commandOrder = resultSet.getInt(COLUMN_ORDER_NUMBER);
                    entries.add(commandOrder,getPackEntryFromResult(resultSet));
                }
                if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                    return returnNull();
                }
                return entries;
            }

            @Override
            public List<Pack.PackEntry> returnNull() {
                return Collections.emptyList();
            }
        }.runQuery(PACKS_GET_CONTENT_BY_ID,null,Map.of(COLUMN_PACK_ID,packId));
    }
    @Override
    public List<Pack> getPacks() {
        return new ExecuteQuery<List<Pack>>() {
            @Override
            public List<Pack> getQuery(final ResultSet resultSet) throws SQLException {
                List<Pack> packs = new ArrayList<>();
                while(resultSet.next()) {
                    packs.add(getPackFromResult(resultSet));
                }
                if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                    return Collections.emptyList();
                }
                return packs;
            }

            @Override
            public List<Pack> returnNull() {
                return Collections.emptyList();
            }
        }.runQuery(PACKS_SELECT_ALL, null,null);
    }

    @Override
    public Set<DropType> getDropTypes() {
        return new ExecuteQuery<Set<DropType>>() {
            @Override
            public Set<DropType> getQuery(final ResultSet resultSet) throws SQLException {
                Set<DropType> customTypes = new HashSet<>();
                while(resultSet.next()) {
                    customTypes.add(getDropTypeFromResult(resultSet));
                }

                if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                    return Collections.emptySet();
                }
                return customTypes;
            }

            @Override
            public Set<DropType> returnNull() {
                return Collections.emptySet();
            }
        }.runQuery(CUSTOM_TYPES_SELECT_ALL, null,null);
    }


    private @NotNull DropType getDropTypeFromResult(final @NotNull ResultSet resultSet) throws SQLException{
        final String typeId = resultSet.getString(COLUMN_TYPE_ID);
        final String type = resultSet.getString(COLUMN_DROP_TYPE);
        final String displayName = resultSet.getString(COLUMN_DISPLAY_NAME);
        return new DropType(typeId,displayName,type);
    }
    @Override
    public DropType getCustomType(final String typeId) {
        ExecuteQuery<DropType> executeQuery = new ExecuteQuery<>() {
            @Override
            public DropType getQuery(final ResultSet resultSet) throws SQLException {
                return getDropTypeFromResult(resultSet);
            }

            @Override
            public DropType returnNull() {
                return null;
            }
        };
        return executeQuery.runQuery(CUSTOM_TYPES_GET_BY_ID,null,Map.of(COLUMN_TYPE_ID,typeId));
    }



    public abstract class ExecuteQuery<T> {
        public T runQuery(final String sql,Map<String, String> values, Map<String,String> where) {
            try (Connection connection = connectionFactory.getConnection()){
                try (PreparedStatement preparedStatement = connection.prepareStatement(statementProcessor.apply(sql,values,where))){
                    try (ResultSet resultSet = preparedStatement.executeQuery()){
                        return getQuery(resultSet);
                    }
                }
            } catch (SQLException e) {
                Util.logSevereException(e);
            }

            return returnNull();
        }
        public abstract T getQuery(ResultSet resultSet) throws SQLException;
        public abstract T returnNull();
    }

    @Override
    public void createCard(final String cardId, final String rarityId, final String seriesId) {
        executeUpdate(CARDS_CREATE,Map.of(COLUMN_CARD_ID,cardId,COLUMN_RARITY_ID,rarityId,COLUMN_SERIES_ID,seriesId) ,null);
    }

    @Override
    public void createRarity(final String rarityId) {
        executeUpdate(RARITY_CREATE,Map.of(COLUMN_RARITY_ID,rarityId),null);
    }

    @Override
    public void createSeries(final String seriesId) {
        executeUpdate(SERIES_CREATE,Map.of(COLUMN_SERIES_ID,seriesId),null);
    }

    @Override
    public void createCustomType(final String typeId, final String type) {
        executeUpdate(CUSTOM_TYPES_CREATE,Map.of(COLUMN_TYPE_ID,typeId,COLUMN_DROP_TYPE,type), null);
    }

    @Override
    public void createPack(final String packId) {
        executeUpdate(PACKS_CREATE,Map.of(COLUMN_PACK_ID, packId),null);
    }

    @Override
    public void reload() {
        //nothing to do here.
    }

    @Override
    public void editCardDisplayName(final String rarityId, final String cardId, final String seriesId, final String displayName) {
        executeUpdate(CARDS_UPDATE_DISPLAY_NAME, Map.of(COLUMN_DISPLAY_NAME,displayName), Map.of(COLUMN_CARD_ID, cardId,COLUMN_RARITY_ID,rarityId,COLUMN_SERIES_ID,seriesId));
    }

    @Override
    public void editCardSeries(final String rarityId, final String cardId, final String seriesId, final Series value) {
        executeUpdate(CARDS_UPDATE_SERIES, Map.of(COLUMN_SERIES_ID, seriesId),Map.of(COLUMN_CARD_ID,cardId,COLUMN_RARITY_ID,rarityId,COLUMN_SERIES_ID,seriesId));
    }

    @Override
    public void editCardSellPrice(final String rarityId, final String cardId, final String seriesId, final double value) {
        executeUpdate(CARDS_UPDATE_SELL_PRICE, Map.of(COLUMN_SELL_PRICE, String.valueOf(value)),Map.of(COLUMN_CARD_ID,cardId,COLUMN_RARITY_ID,rarityId,COLUMN_SERIES_ID,seriesId));
    }

    @Override
    public void editCardType(final String rarityId, final String cardId, final String seriesId, final DropType value) {
        executeUpdate(CARDS_UPDATE_TYPE, Map.of(COLUMN_TYPE_ID, String.valueOf(value)),Map.of(COLUMN_CARD_ID,cardId,COLUMN_RARITY_ID,rarityId,COLUMN_SERIES_ID,seriesId));
    }

    @Override
    public void editCardInfo(final String rarityId, final String cardId, final String seriesId, final String value) {
        executeUpdate(CARDS_UPDATE_INFO, Map.of("info", String.valueOf(value)),Map.of(COLUMN_CARD_ID,cardId,COLUMN_RARITY_ID,rarityId,COLUMN_SERIES_ID,seriesId));
    }

    @Override
    public void editCardCustomModelData(final String rarityId, final String cardId, final String seriesId, final int value) {
        executeUpdate(CARDS_UPDATE_CUSTOM_MODEL_DATA, Map.of(COLUMN_CUSTOM_MODEL_DATA, String.valueOf(value)),Map.of(COLUMN_CARD_ID,cardId,COLUMN_RARITY_ID,rarityId,COLUMN_SERIES_ID,seriesId));
    }

    @Override
    public void editCardBuyPrice(final String rarityId, final String cardId, final String seriesId, final double value) {
        executeUpdate(CARDS_UPDATE_BUY_PRICE, Map.of(COLUMN_BUY_PRICE, String.valueOf(value)),Map.of(COLUMN_CARD_ID,cardId,COLUMN_RARITY_ID,rarityId,COLUMN_SERIES_ID,seriesId));
    }

    @Override
    public void editRarityBuyPrice(final String rarityId, final double buyPrice) {
        executeUpdate(RARITY_UPDATE_BUY_PRICE, Map.of(COLUMN_BUY_PRICE, String.valueOf(buyPrice)),Map.of(COLUMN_RARITY_ID,rarityId));
    }

    @Override
    public void editRarityAddReward(final String rarityId, final String reward) {
        executeUpdate(REWARDS_UPDATE_ADD_REWARD, Map.of("reward", reward),Map.of(COLUMN_RARITY_ID,rarityId));//todo
    }

    @Override
    public void editRarityDefaultColor(final String rarityId, final String defaultColor) {
        executeUpdate(RARITY_UPDATE_DEFAULT_COLOR, Map.of(COLUMN_DEFAULT_COLOR, defaultColor),Map.of(COLUMN_RARITY_ID,rarityId));
    }

    @Override
    public void editRarityDisplayName(final String rarityId, final String displayName) {
        executeUpdate(RARITY_UPDATE_DISPLAY_NAME, Map.of(COLUMN_DISPLAY_NAME, displayName),Map.of(COLUMN_RARITY_ID,rarityId));
    }

    @Override
    public void editRaritySellPrice(final String rarityId, final double sellPrice) {
        executeUpdate(RARITY_UPDATE_SELL_PRICE, Map.of(COLUMN_SELL_PRICE, String.valueOf(sellPrice)),Map.of(COLUMN_RARITY_ID,rarityId));
    }

    @Override
    public void editRarityRemoveAllRewards(final String rarityId) {
        executeUpdate(REWARDS_UPDATE_REMOVE_ALL_REWARDS,null ,Map.of(COLUMN_RARITY_ID,rarityId));
    }

    @Override
    public void editRarityRemoveReward(final String rarityId, final int rewardNumber) {
        executeUpdate(REWARDS_UPDATE_REMOVE_REWARD,null,Map.of(COLUMN_RARITY_ID,rarityId,COLUMN_ORDER_NUMBER,String.valueOf(rewardNumber)));
    }

    @Override
    public void editSeriesDisplayName(final String seriesId, final String displayName) {
        executeUpdate(SERIES_UPDATE_DISPLAY_NAME, Map.of(COLUMN_DISPLAY_NAME,displayName),Map.of(COLUMN_SERIES_ID,seriesId));
    }

    @Override
    public void editSeriesColors(final String seriesId, final ColorSeries colors) {
        executeUpdate(SERIES_UPDATE_COLORS,Map.of(COLUMN_COLOR_TYPE,colors.getType(),COLUMN_COLOR_INFO,colors.getInfo(),COLUMN_COLOR_ABOUT,colors.getAbout(),COLUMN_COLOR_RARITY,colors.getRarity(),COLUMN_COLOR_SERIES,colors.getSeries()),Map.of(COLUMN_SERIES_ID,seriesId));
    }

    @Override
    public void editSeriesMode(final String seriesId, final Mode mode) {
        executeUpdate(SERIES_UPDATE_MODE,Map.of(COLUMN_MODE, mode.toString()),Map.of(COLUMN_SERIES_ID,seriesId));
    }

    @Override
    public void editCustomTypeDisplayName(final String typeId, final String displayName) {
        executeUpdate(CUSTOM_TYPES_UPDATE_DISPLAY_NAME, Map.of(COLUMN_DISPLAY_NAME, displayName),Map.of(COLUMN_TYPE_ID,typeId));
    }

    @Override
    public void editCustomTypeType(final String typeId, final String type) {
        executeUpdate(CUSTOM_TYPES_UPDATE_TYPE,Map.of(COLUMN_DROP_TYPE, type),Map.of(COLUMN_TYPE_ID,typeId));
    }

    @Override
    public void editPackDisplayName(final String packId, final String displayName) {
        executeUpdate(PACKS_UPDATE_DISPLAY_NAME,Map.of(COLUMN_DISPLAY_NAME, displayName), Map.of(COLUMN_PACK_ID, packId));
    }

    @Override
    public void editPackContents(final String packId, final int lineNumber, final Pack.PackEntry packEntry) {
        //TODO
    }

    @Override
    public void editPackPermission(final String packId, final String permission) {
        executeUpdate(PACKS_UPDATE_PERMISSION,Map.of(COLUMN_PACK_PERMISSION, permission), Map.of(COLUMN_PACK_ID,packId));
    }

    @Override
    public void editPackPrice(final String packId, final double price) {
        executeUpdate(PACKS_UPDATE_PRICE,Map.of(COLUMN_BUY_PRICE,String.valueOf(price)),Map.of(COLUMN_PACK_ID,packId));
    }

    private void executeUpdate(@NotNull String sql,@Nullable Map<String,String> values,@Nullable Map<String,String> where) {
        try (Connection connection = connectionFactory.getConnection()){
            try (PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(sql, values,where))){
                statement.executeUpdate();
                plugin.debug(SqlStorage.class, "Run SQL:");
                plugin.debug(SqlStorage.class,sql);
                if(values != null)
                    plugin.debug(SqlStorage.class, values.toString());
                if(where != null)
                    plugin.debug(SqlStorage.class, where.toString());
            }
        } catch (SQLException e) {
            Util.logSevereException(e);
        }
    }
}
