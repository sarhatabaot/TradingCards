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
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.enums.CustomTypesDropType;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.enums.SeriesSeriesMode;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Cards;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.CustomTypes;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Decks;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Packs;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.PacksContent;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Rarities;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Rewards;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.SeriesColors;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.PacksContentRecord;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql.ConnectionFactory;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql.SchemaReader;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Results;
import org.jooq.SQLDialect;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.MappedTable;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

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
    private static final String COLUMN_COLOR_RARITY = "rarity";
    private static final String COLUMN_COLOR_SERIES = "series";

    private final TradingCards plugin;
    private final ConnectionFactory connectionFactory;
    private final StatementProcessor statementProcessor;
    private final Settings jooqSettings;
    private final StorageType storageType;


    @Override
    public void init(final TradingCards plugin) {
        connectionFactory.init(plugin);
        try {
            applySchema();
        } catch (SQLException | IOException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public StatementProcessor getStatementProcessor() {
        return statementProcessor;
    }

    public SqlStorage(final TradingCards plugin, final String tablePrefix, final String dbName, final ConnectionFactory connectionFactory, final StorageType storageType) {
        this.plugin = plugin;
        this.connectionFactory = connectionFactory;
        this.storageType = storageType;
        this.statementProcessor = new StatementProcessor(tablePrefix, plugin);
        this.jooqSettings = new Settings();
        initJooqSettings(tablePrefix, dbName);
    }

    private void initJooqSettings(final String tablePrefix, final String dbName) {
        MappedTable mappedTable = new MappedTable();
        mappedTable.withOutput(tablePrefix + mappedTable.getInput());
        jooqSettings.withRenderMapping(
                new RenderMapping().withSchemata(
                        new MappedSchema().withInput("minecraft")
                                .withOutput(dbName)
                                .withTables(mappedTable)
                )
        );
    }

    @Override
    public List<Deck> getPlayerDecks(final @NotNull UUID playerUuid) {
        return new ExecuteQuery<List<Deck>, Results>(this, jooqSettings) {
            @Override
            public List<Deck> onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(Decks.DECKS).where(Decks.DECKS.UUID.eq(playerUuid.toString())).fetchMany());
            }

            @Override
            public List<Deck> getQuery(final ResultSet resultSet) {
                return null;
            }

            @Override
            public @NotNull List<Deck> getQuery(final @NotNull Results results) throws SQLException {
                List<Deck> decks = new ArrayList<>();
                for (Result<Record> recordResult : results) {
                    decks.add(getDeckFromRecord(recordResult));
                }
                return decks;
            }

            @Override
            public List<Deck> returnNull() {
                return Collections.emptyList();
            }
        }.prepareAndRunQuery();
    }

    @Override
    public Deck getDeck(final @NotNull UUID playerUuid, final int deckNumber) {
        return new ExecuteQuery<Deck, Results>(this, jooqSettings) {
            @Override
            public Deck onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(Decks.DECKS)
                        .where(Decks.DECKS.UUID.eq(playerUuid.toString())
                                .and(Decks.DECKS.DECK_NUMBER.eq(deckNumber))).fetchMany());
            }

            @Override
            public @NotNull Deck getQuery(final @NotNull Results recordResult) throws SQLException {
                if (recordResult.isEmpty()) {
                    plugin.debug(getClass(), "Could not find a deck for uuid=" + playerUuid + ",decknumber=" + deckNumber);
                    return new Deck(playerUuid, deckNumber, new ArrayList<>());
                }
                return getDeckFromRecord(recordResult.get(0));
            }

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
        }.prepareAndRunQuery();
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


    private Deck getDeckFromRecord(Result<Record> recordResult) throws SQLException {
//        final String playerUuid = recordResult.getValue(Decks.DECKS.UUID); todo for now
//        final int deckNumber = recordResult.getValue(Decks.DECKS.ID);
        return getDeckFromResultSet(recordResult.intoResultSet());
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
        return new ExecuteQuery<Rarity, Results>(this, jooqSettings) {

            @Override
            public Rarity onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(Rarities.RARITIES)
                        .where(Rarities.RARITIES.RARITY_ID.eq(rarityId)).fetchMany());
            }

            @Override
            public Rarity getQuery(final @NotNull Results results) {
                if (results.isEmpty()) {
                    plugin.getLogger().info("No such rarity " + rarityId);
                    return returnNull();
                }

                final Result<Record> recordResult = results.get(0);
                final String displayName = recordResult.getValue(Rarities.RARITIES.DISPLAY_NAME);
                final String defaultColor = recordResult.getValue(Rarities.RARITIES.DEFAULT_COLOR);
                final List<String> rewards = getRewards(rarityId);
                final double buyPrice = recordResult.getValue(Rarities.RARITIES.BUY_PRICE);
                final double sellPrice = recordResult.getValue(Rarities.RARITIES.SELL_PRICE);
                return new Rarity(rarityId, displayName, defaultColor, buyPrice, sellPrice, rewards);
            }

            @Override
            public Rarity getQuery(final ResultSet resultSet) throws SQLException {
                return returnNull();
            }

            @Contract(pure = true)
            @Override
            public @Nullable Rarity returnNull() {
                return null;
            }
        }.prepareAndRunQuery();
    }

    @Override
    public List<String> getRewards(final String rarityId) {
        return new ExecuteQuery<List<String>>(this, jooqSettings) {

            @Override
            public List<String> onRunQuery(final DSLContext dslContext) throws SQLException {
                Result<Record> result = dslContext.select().from(Rewards.REWARDS)
                        .where(Rewards.REWARDS.RARITY_ID.eq(rarityId)).fetch();
                return getQuery(result);
            }

            @Override
            public @NotNull List<String> getQuery(final @NotNull Result<Record> result) throws SQLException {
                return getQuery(result);
            }

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

            @Contract(pure = true)
            @Override
            public @NotNull @Unmodifiable List<String> returnNull() {
                return Collections.emptyList();
            }
        }.prepareAndRunQuery();
    }

    @Override
    public Series getSeries(final String seriesId) {
        return new ExecuteQuery<Series>(this, jooqSettings) {
            @Override
            public Series onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES).where(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES.SERIES_ID.eq(seriesId)).fetch());
            }

            @Override
            public Series getQuery(final @NotNull Result<Record> result) throws SQLException {
                return getQuery(result.intoResultSet());
            }

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
        }.prepareAndRunQuery();
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
        byte isShiny = (byte) (storageEntry.isShiny() ? 1 : 0);
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Decks.DECKS)
                        .set(Decks.DECKS.CARD_ID, storageEntry.getCardId())
                        .set(Decks.DECKS.RARITY_ID, storageEntry.getRarityId())
                        .set(Decks.DECKS.IS_SHINY, isShiny)
                        .set(Decks.DECKS.AMOUNT, storageEntry.getAmount())
                        .where(Decks.DECKS.UUID.eq(playerUuid.toString()))
                        .and(Decks.DECKS.DECK_NUMBER.eq(deckNumber))
                        .and(Decks.DECKS.CARD_ID.eq(storageEntry.getCardId()))
                        .and(Decks.DECKS.RARITY_ID.eq(storageEntry.getRarityId()))
                        .and(Decks.DECKS.IS_SHINY.eq(isShiny))
                        .execute();
                plugin.debug(SqlStorage.class, "(UPDATE) " + storageEntry);
            }
        }.executeUpdate();
    }

    @Override
    public boolean hasCard(final UUID playerUuid, final String card, final String rarity) {
        return new ExecuteQuery<Boolean>(this, jooqSettings) {
            @Override
            public Boolean onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(Decks.DECKS).where(Decks.DECKS.UUID.eq(playerUuid.toString()).and(Decks.DECKS.CARD_ID.eq(card).and(Decks.DECKS.RARITY_ID.eq(rarity)).and(Decks.DECKS.IS_SHINY.eq((byte) 0)))).fetch());
            }

            @Override
            public Boolean getQuery(final ResultSet resultSet) {
                return null;
            }

            @Override
            public Boolean getQuery(final @NotNull Result<Record> result) {
                return result.isNotEmpty();
            }

            @Override
            public Boolean returnNull() {
                return false;
            }
        }.prepareAndRunQuery();
    }

    @Override
    public boolean hasShinyCard(final UUID playerUuid, final String card, final String rarity) {
        return new ExecuteQuery<Boolean>(this, jooqSettings) {
            @Override
            public Boolean onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(Decks.DECKS).where(Decks.DECKS.UUID.eq(playerUuid.toString()).and(Decks.DECKS.CARD_ID.eq(card).and(Decks.DECKS.RARITY_ID.eq(rarity)).and(Decks.DECKS.IS_SHINY.eq((byte) 1)))).fetch());
            }

            @Override
            public Boolean getQuery(final ResultSet resultSet) throws SQLException {
                return null;
            }

            @Override
            public Boolean getQuery(final @NotNull Result<Record> result) throws SQLException {
                return result.isNotEmpty();
            }

            @Override
            public Boolean returnNull() {
                return false;
            }
        }.prepareAndRunQuery();
    }

    @Override
    public StorageType getType() {
        return storageType;
    }


    public void addCardToDeck(final UUID playerUuid, final int deckNumber, final @NotNull StorageEntry entry) {
        //TODO, add series_id to decks.
        final String cardId = entry.getCardId();
        final String rarityId = entry.getRarityId();
        final int amount = entry.getAmount();
        final byte isShiny = (byte) (entry.isShiny() ? 1 : 0);
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.insertInto(Decks.DECKS)
                        .set(Decks.DECKS.UUID, playerUuid.toString())
                        .set(Decks.DECKS.DECK_NUMBER, deckNumber)
                        .set(Decks.DECKS.CARD_ID, cardId)
                        .set(Decks.DECKS.RARITY_ID, rarityId)
                        .set(Decks.DECKS.AMOUNT, amount)
                        .set(Decks.DECKS.IS_SHINY, isShiny)
                        .execute();
            }
        }.executeUpdate();
    }

    public void removeCardFromDeck(final UUID playerUuid, final int deckNumber, final @NotNull StorageEntry entry) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.deleteFrom(Decks.DECKS)
                        .where(Decks.DECKS.UUID.eq(playerUuid.toString()))
                        .and(Decks.DECKS.DECK_NUMBER.eq(deckNumber))
                        .and(Decks.DECKS.CARD_ID.eq(entry.getCardId()))
                        .and(Decks.DECKS.RARITY_ID.eq(entry.getRarityId()))
                        .and(Decks.DECKS.AMOUNT.eq(entry.getAmount()))
                        .and(Decks.DECKS.IS_SHINY.eq((byte) (entry.isShiny() ? 1 : 0))).execute();
                plugin.debug(SqlStorage.class, "(REMOVE) " + entry);
            }
        }.executeUpdate();
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
        return new ExecuteQuery<List<Rarity>>(this, jooqSettings) {
            @Override
            public List<Rarity> onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(Rarities.RARITIES).fetch());
            }


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
            public List<Rarity> getQuery(final @NotNull Result<Record> result) throws SQLException {
                return getQuery(result.intoResultSet());
            }

            @Contract(pure = true)
            @Override
            public @NotNull @Unmodifiable List<Rarity> returnNull() {
                return Collections.emptyList();
            }
        }.prepareAndRunQuery();
    }


    @Override
    public Collection<Series> getAllSeries() {
        return new ExecuteQuery<Collection<Series>>(this, jooqSettings) {
            @Override
            public Collection<Series> onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES).fetch());
            }

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
            public Collection<Series> getQuery(final @NotNull Result<Record> result) throws SQLException {
                return getQuery(result.intoResultSet());
            }

            @Contract(pure = true)
            @Override
            public @NotNull @Unmodifiable Collection<Series> returnNull() {
                return Collections.emptyList();
            }
        }.prepareAndRunQuery();
    }

    @Contract("_ -> new")
    private @NotNull ColorSeries getColorSeries(final String seriesId) {
        return new ExecuteQuery<ColorSeries>(this, jooqSettings) {
            @Override
            public ColorSeries onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(SeriesColors.SERIES_COLORS).where(SeriesColors.SERIES_COLORS.SERIES_ID.eq(seriesId)).fetch());
            }

            @Override
            public ColorSeries getQuery(final @NotNull Result<Record> result) throws SQLException {
                return getQuery(result.intoResultSet());
            }

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
        }.prepareAndRunQuery();
    }

    @Override
    public Set<Series> getActiveSeries() {
        return new ExecuteQuery<Set<Series>>(this, jooqSettings) {
            @Override
            public Set<Series> onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES).where(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES.SERIES_MODE.eq(SeriesSeriesMode.ACTIVE)).fetch());
            }

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
            public Set<Series> getQuery(final @NotNull Result<Record> result) throws SQLException {
                return getQuery(result.intoResultSet());
            }

            @Override
            public Set<Series> returnNull() {
                return Collections.emptySet();
            }
        }.prepareAndRunQuery();
    }

    @Override
    public Map<String, TradingCard> getCardsMap() {
        Map<String, TradingCard> cardsMap = new HashMap<>();
        for (TradingCard tradingCard : getCards()) {
            final String cardId = tradingCard.getCardName();
            final String rarityId = tradingCard.getRarity().getName();
            final String cardKey = CardUtil.cardKey(rarityId, cardId);
            cardsMap.put(cardKey, tradingCard);
        }
        return cardsMap;
    }

    @Override
    public Map<String, TradingCard> getActiveCardsMap() {
        return null;
    }

    @Override
    public List<TradingCard> getCards() {
        return new ExecuteQuery<List<TradingCard>>(this, jooqSettings) {
            @Override
            public List<TradingCard> onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(Cards.CARDS).fetch());
            }

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
            public List<TradingCard> getQuery(final @NotNull Result<Record> result) throws SQLException {
                return getQuery(result.intoResultSet());
            }

            @Override
            public List<TradingCard> returnNull() {
                return Collections.emptyList();
            }
        }.prepareAndRunQuery();
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
        return new ExecuteQuery<List<TradingCard>>(this, jooqSettings) {
            @Override
            public List<TradingCard> onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().where(Cards.CARDS.RARITY_ID.eq(rarityId)).fetch());
            }

            @Override
            public List<TradingCard> getQuery(final @NotNull Result<Record> result) throws SQLException {
                return getQuery(result.intoResultSet());
            }

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
        }.prepareAndRunQuery();
    }

    @Override
    public List<TradingCard> getCardsInSeries(final String seriesId) {
        return new ExecuteQuery<List<TradingCard>>(this, jooqSettings) {

            @Override
            public List<TradingCard> getQuery(final @NotNull Result<Record> result) throws SQLException {
                return getQuery(result.intoResultSet());
            }

            @Override
            public List<TradingCard> onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(Cards.CARDS).where(Cards.CARDS.SERIES_ID.eq(seriesId)).fetch());
            }

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

            @Contract(pure = true)
            @Override
            public @NotNull @Unmodifiable List<TradingCard> returnNull() {
                return Collections.emptyList();
            }
        }.prepareAndRunQuery();
    }

    @Override
    public List<TradingCard> getCardsInRarityAndSeries(final String rarityId, final String seriesId) {
        return new ExecuteQuery<List<TradingCard>>(this, jooqSettings) {
            @Override
            public List<TradingCard> onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(Cards.CARDS).where(Cards.CARDS.RARITY_ID.eq(rarityId).and(Cards.CARDS.SERIES_ID.eq(seriesId))).fetch());
            }

            @Override
            public List<TradingCard> getQuery(final @NotNull Result<Record> result) throws SQLException {
                return getQuery(result.intoResultSet());
            }

            @Override
            public List<TradingCard> getQuery(final ResultSet resultSet) throws SQLException {
                List<TradingCard> cards = new ArrayList<>();
                while (resultSet.next()) {
                    cards.add(getTradingCardFromResult(resultSet));
                }
                if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                    return returnNull();
                }
                return cards;
            }

            @Contract(pure = true)
            @Override
            public @NotNull @Unmodifiable List<TradingCard> returnNull() {
                return Collections.emptyList();
            }
        }.prepareAndRunQuery();
    }

    @Override
    public List<TradingCard> getActiveCards() {
        List<TradingCard> activeCards = new ArrayList<>();
        for (Series series : getActiveSeries()) {
            activeCards = Stream.concat(activeCards.stream(), getCardsInSeries(series.getName()).stream()).toList();
        }
        return activeCards;
    }

    @Override
    public Card<TradingCard> getCard(final String cardId, final String rarityId) {
        return new ExecuteQuery<TradingCard>(this, jooqSettings) {
            @Override
            public TradingCard onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(Cards.CARDS).where(Cards.CARDS.CARD_ID.eq(cardId).and(Cards.CARDS.RARITY_ID.eq(rarityId))).fetch());
            }

            @Override
            public TradingCard getQuery(final @NotNull Result<Record> result) throws SQLException {
                return getQuery(result.intoResultSet());
            }

            @Override
            public TradingCard getQuery(final ResultSet resultSet) throws SQLException {
                if (resultSet.next()) {
                    return getTradingCardFromResult(resultSet);
                }
                return returnNull();
            }

            @Contract(" -> new")
            @Override
            public @NotNull TradingCard returnNull() {
                return new EmptyCard();
            }
        }.prepareAndRunQuery();
    }

    @Override
    public Card<TradingCard> getActiveCard(final String cardId, final String rarityId) {
        //TODO
        return null;
    }

    @Override
    public @Nullable Pack getPack(final String packsId) {
        return new ExecuteQuery<Pack>(this, jooqSettings) {
            @Override
            public Pack onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(Packs.PACKS).where(Packs.PACKS.PACK_ID.eq(packsId)).fetch());
            }

            @Override
            public Pack getQuery(final @NotNull Result<Record> result) throws SQLException {
                return getQuery(result.intoResultSet());
            }

            @Override
            public Pack getQuery(final ResultSet resultSet) throws SQLException {
                if (resultSet.next()) {
                    return getPackFromResult(resultSet);
                }
                return returnNull();
            }

            @Contract(pure = true)
            @Override
            public @Nullable Pack returnNull() {
                return null;
            }
        }.prepareAndRunQuery();
    }


    private @NotNull Pack getPackFromResult(@NotNull ResultSet resultSet) throws SQLException {
        final String id = resultSet.getString(COLUMN_PACK_ID);
        final String displayName = resultSet.getString(COLUMN_DISPLAY_NAME);
        final double price = resultSet.getDouble(COLUMN_BUY_PRICE);
        final String permission = resultSet.getString(COLUMN_PACK_PERMISSION);
        final List<Pack.PackEntry> entries = getPackEntries(id);
        return new Pack(id, entries, displayName, price, permission);
    }

    private Pack.@NotNull PackEntry getPackEntryFromResult(@NotNull ResultSet resultSet) throws SQLException {
        final String rarityId = resultSet.getString(COLUMN_RARITY_ID);
        final String seriesId = resultSet.getString(COLUMN_SERIES_ID);
        final int cardAmount = resultSet.getInt(COLUMN_CARD_AMOUNT);
        return new Pack.PackEntry(rarityId, cardAmount, seriesId);
    }

    private @NotNull @Unmodifiable List<Pack.PackEntry> getPackEntries(final String packId) {
        return new ExecuteQuery<List<Pack.PackEntry>>(this, jooqSettings) {

            @Override
            public List<Pack.PackEntry> onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(PacksContent.PACKS_CONTENT).where(PacksContent.PACKS_CONTENT.PACK_ID.eq(packId)).fetch());
            }

            @Override
            public List<Pack.PackEntry> getQuery(final @NotNull Result<Record> result) throws SQLException {
                return getQuery(result.intoResultSet());
            }

            @Override
            public List<Pack.PackEntry> getQuery(final ResultSet resultSet) throws SQLException {
                List<Pack.PackEntry> entries = new ArrayList<>();
                while (resultSet.next()) {
                    int commandOrder = resultSet.getInt(COLUMN_ORDER_NUMBER);
                    entries.add(commandOrder, getPackEntryFromResult(resultSet));
                }
                if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                    return returnNull();
                }
                return entries;
            }

            @Contract(pure = true)
            @Override
            public @NotNull @Unmodifiable List<Pack.PackEntry> returnNull() {
                return Collections.emptyList();
            }
        }.prepareAndRunQuery();
    }

    @Override
    public List<Pack> getPacks() {
        return new ExecuteQuery<List<Pack>>(this, jooqSettings) {
            @Override
            public List<Pack> onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(Packs.PACKS).fetch());
            }

            @Override
            public List<Pack> getQuery(final @NotNull Result<Record> result) throws SQLException {
                return getQuery(result.intoResultSet());
            }

            @Override
            public List<Pack> getQuery(final ResultSet resultSet) throws SQLException {
                List<Pack> packs = new ArrayList<>();
                while (resultSet.next()) {
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
        }.prepareAndRunQuery();
    }

    @Override
    public Set<DropType> getDropTypes() {
        return new ExecuteQuery<Set<DropType>>(this, jooqSettings) {
            @Override
            public Set<DropType> onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(CustomTypes.CUSTOM_TYPES).fetch());
            }

            @Override
            public Set<DropType> getQuery(final @NotNull Result<Record> result) throws SQLException {
                return getQuery(result.intoResultSet());
            }

            @Override
            public Set<DropType> getQuery(final ResultSet resultSet) throws SQLException {
                Set<DropType> customTypes = new HashSet<>();
                while (resultSet.next()) {
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
        }.prepareAndRunQuery();
    }


    private @NotNull DropType getDropTypeFromResult(final @NotNull ResultSet resultSet) throws SQLException {
        final String typeId = resultSet.getString(COLUMN_TYPE_ID);
        final String type = resultSet.getString(COLUMN_DROP_TYPE);
        final String displayName = resultSet.getString(COLUMN_DISPLAY_NAME);
        return new DropType(typeId, displayName, type);
    }

    @Override
    public DropType getCustomType(final String typeId) {
        ExecuteQuery<DropType> executeQuery = new ExecuteQuery<>(this, jooqSettings) {
            @Override
            public DropType onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select().from(CustomTypes.CUSTOM_TYPES).where(CustomTypes.CUSTOM_TYPES.TYPE_ID.eq(typeId)).fetch());
            }

            @Override
            public DropType getQuery(final @NotNull Result<Record> result) throws SQLException {
                return getQuery(result.intoResultSet());
            }

            @Override
            public @NotNull DropType getQuery(final ResultSet resultSet) throws SQLException {
                return getDropTypeFromResult(resultSet);
            }

            @Contract(pure = true)
            @Override
            public @Nullable DropType returnNull() {
                return null;
            }
        };
        return executeQuery.prepareAndRunQuery();
    }

    @Override
    public void reload() {
        //nothing to do here.
    }

    @Override
    public void createCard(final String cardId, final String rarityId, final String seriesId) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.insertInto(Cards.CARDS)
                        .set(Cards.CARDS.CARD_ID, cardId)
                        .set(Cards.CARDS.SERIES_ID, seriesId)
                        .set(Cards.CARDS.RARITY_ID, rarityId)
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void createRarity(final String rarityId) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.insertInto(Rarities.RARITIES)
                        .set(Rarities.RARITIES.RARITY_ID, rarityId)
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void createSeries(final String seriesId) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.insertInto(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES)
                        .set(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES.SERIES_ID, seriesId)
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void createCustomType(final String typeId, final String type) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.insertInto(CustomTypes.CUSTOM_TYPES)
                        .set(CustomTypes.CUSTOM_TYPES.TYPE_ID, type)
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void createPack(final String packId) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.insertInto(Packs.PACKS)
                        .set(Packs.PACKS.PACK_ID, packId)
                        .execute();
            }
        }.executeUpdate();
    }


    @Override
    public void editCardDisplayName(final String rarityId, final String cardId, final String seriesId, final String displayName) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Cards.CARDS)
                        .set(Cards.CARDS.DISPLAY_NAME, displayName)
                        .where(Cards.CARDS.RARITY_ID.eq(rarityId))
                        .and(Cards.CARDS.CARD_ID.eq(cardId))
                        .and(Cards.CARDS.SERIES_ID.eq(seriesId))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editCardSeries(final String rarityId, final String cardId, final String seriesId, final Series value) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Cards.CARDS)
                        .set(Cards.CARDS.SERIES_ID, value.getName())
                        .where(Cards.CARDS.RARITY_ID.eq(rarityId))
                        .and(Cards.CARDS.CARD_ID.eq(cardId))
                        .and(Cards.CARDS.SERIES_ID.eq(seriesId))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editCardSellPrice(final String rarityId, final String cardId, final String seriesId, final double value) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Cards.CARDS)
                        .set(Cards.CARDS.SELL_PRICE, value)
                        .where(Cards.CARDS.RARITY_ID.eq(rarityId))
                        .and(Cards.CARDS.CARD_ID.eq(cardId))
                        .and(Cards.CARDS.SERIES_ID.eq(seriesId))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editCardType(final String rarityId, final String cardId, final String seriesId, final DropType value) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Cards.CARDS)
                        .set(Cards.CARDS.TYPE_ID, value.getId())
                        .where(Cards.CARDS.RARITY_ID.eq(rarityId))
                        .and(Cards.CARDS.CARD_ID.eq(cardId))
                        .and(Cards.CARDS.SERIES_ID.eq(seriesId))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editCardInfo(final String rarityId, final String cardId, final String seriesId, final String value) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Cards.CARDS)
                        .set(Cards.CARDS.INFO, value)
                        .where(Cards.CARDS.RARITY_ID.eq(rarityId))
                        .and(Cards.CARDS.CARD_ID.eq(cardId))
                        .and(Cards.CARDS.SERIES_ID.eq(seriesId))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editCardCustomModelData(final String rarityId, final String cardId, final String seriesId, final int value) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Cards.CARDS)
                        .set(Cards.CARDS.CUSTOM_MODEL_DATA, value)
                        .where(Cards.CARDS.RARITY_ID.eq(rarityId))
                        .and(Cards.CARDS.CARD_ID.eq(cardId))
                        .and(Cards.CARDS.SERIES_ID.eq(seriesId))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editCardBuyPrice(final String rarityId, final String cardId, final String seriesId, final double value) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Cards.CARDS)
                        .set(Cards.CARDS.BUY_PRICE, value)
                        .where(Cards.CARDS.RARITY_ID.eq(rarityId))
                        .and(Cards.CARDS.CARD_ID.eq(cardId))
                        .and(Cards.CARDS.SERIES_ID.eq(seriesId))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editRarityBuyPrice(final String rarityId, final double buyPrice) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Rarities.RARITIES)
                        .set(Rarities.RARITIES.BUY_PRICE, buyPrice)
                        .where(Rarities.RARITIES.RARITY_ID.eq(rarityId))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editRarityAddReward(final String rarityId, final String reward) {
        int commandOrder = getRewards(rarityId).size() - 1;
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Rewards.REWARDS)
                        .set(Rewards.REWARDS.COMMAND, reward)
                        .set(Rewards.REWARDS.COMMAND_ORDER, commandOrder)
                        .where(Rewards.REWARDS.RARITY_ID.eq(rarityId))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editRarityDefaultColor(final String rarityId, final String defaultColor) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Rarities.RARITIES)
                        .set(Rarities.RARITIES.DEFAULT_COLOR, defaultColor)
                        .where(Rarities.RARITIES.RARITY_ID.eq(rarityId))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editRarityDisplayName(final String rarityId, final String displayName) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Rarities.RARITIES)
                        .set(Rarities.RARITIES.DISPLAY_NAME, displayName)
                        .where(Rarities.RARITIES.RARITY_ID.eq(rarityId))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editRaritySellPrice(final String rarityId, final double sellPrice) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Rarities.RARITIES)
                        .set(Rarities.RARITIES.SELL_PRICE, sellPrice)
                        .where(Rarities.RARITIES.RARITY_ID.eq(rarityId))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editRarityRemoveAllRewards(final String rarityId) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.deleteFrom(Rewards.REWARDS)
                        .where(Rewards.REWARDS.RARITY_ID.eq(rarityId))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editRarityRemoveReward(final String rarityId, final int rewardNumber) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.deleteFrom(Rewards.REWARDS)
                        .where(Rewards.REWARDS.RARITY_ID.eq(rarityId))
                        .and(Rewards.REWARDS.COMMAND_ORDER.eq(rewardNumber))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editSeriesDisplayName(final String seriesId, final String displayName) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES)
                        .set(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES.DISPLAY_NAME, displayName)
                        .where(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES.SERIES_ID.eq(seriesId))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editSeriesColors(final String seriesId, final @NotNull ColorSeries colors) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(SeriesColors.SERIES_COLORS)
                        .set(SeriesColors.SERIES_COLORS.SERIES, colors.getSeries())
                        .set(SeriesColors.SERIES_COLORS.ABOUT, colors.getAbout())
                        .set(SeriesColors.SERIES_COLORS.INFO, colors.getInfo())
                        .set(SeriesColors.SERIES_COLORS.RARITY, colors.getRarity())
                        .set(SeriesColors.SERIES_COLORS.TYPE, colors.getType())
                        .where(SeriesColors.SERIES_COLORS.SERIES_ID.eq(seriesId))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editSeriesMode(final String seriesId, final @NotNull Mode mode) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES)
                        .set(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES.SERIES_MODE, SeriesSeriesMode.lookupLiteral(mode.name()))
                        .where(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES.SERIES_ID.eq(seriesId))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editCustomTypeDisplayName(final String typeId, final String displayName) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(CustomTypes.CUSTOM_TYPES)
                        .set(CustomTypes.CUSTOM_TYPES.DISPLAY_NAME, displayName)
                        .where(CustomTypes.CUSTOM_TYPES.TYPE_ID.eq(typeId)).execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editCustomTypeType(final String typeId, final String type) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(CustomTypes.CUSTOM_TYPES)
                        .set(CustomTypes.CUSTOM_TYPES.DROP_TYPE, CustomTypesDropType.lookupLiteral(type))
                        .where(CustomTypes.CUSTOM_TYPES.TYPE_ID.eq(typeId)).execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editPackDisplayName(final String packId, final String displayName) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Packs.PACKS)
                        .set(Packs.PACKS.DISPLAY_NAME, displayName)
                        .where(Packs.PACKS.PACK_ID.eq(packId)).execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editPackContents(final String packId, final int lineNumber, final Pack.@NotNull PackEntry packEntry) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(PacksContent.PACKS_CONTENT)
                        .set(PacksContent.PACKS_CONTENT.PACK_ID, packId)
                        .set(PacksContent.PACKS_CONTENT.LINE_NUMBER, lineNumber)
                        .set(PacksContent.PACKS_CONTENT.SERIES_ID, packEntry.seriesId())
                        .set(PacksContent.PACKS_CONTENT.RARITY_ID, packEntry.getRarityId())
                        .set(PacksContent.PACKS_CONTENT.CARD_AMOUNT, String.valueOf(packEntry.getAmount())).execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editPackContentsAdd(final String packId, final Pack.@NotNull PackEntry packEntry) {
        final int lineNumber = getPackEntries(packId).size();
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.insertInto(PacksContent.PACKS_CONTENT)
                        .set(PacksContent.PACKS_CONTENT.PACK_ID, packId)
                        .set(PacksContent.PACKS_CONTENT.LINE_NUMBER, lineNumber)
                        .set(PacksContent.PACKS_CONTENT.SERIES_ID, packEntry.seriesId())
                        .set(PacksContent.PACKS_CONTENT.RARITY_ID, packEntry.getRarityId())
                        .set(PacksContent.PACKS_CONTENT.CARD_AMOUNT, String.valueOf(packEntry.getAmount())).execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editPackContentsDelete(final String packId, final int lineNumber) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.deleteFrom(PacksContent.PACKS_CONTENT).where(PacksContent.PACKS_CONTENT.PACK_ID.eq(packId).and(PacksContent.PACKS_CONTENT.LINE_NUMBER.eq(lineNumber))).execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editPackPermission(final String packId, final String permission) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Packs.PACKS).set(Packs.PACKS.PERMISSION, permission).where(Packs.PACKS.PACK_ID.eq(packId)).execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editPackPrice(final String packId, final double price) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Packs.PACKS).set(Packs.PACKS.BUY_PRICE, price).where(Packs.PACKS.PACK_ID.eq(packId)).execute();
            }
        }.executeUpdate();
    }

    private void executeUpdate(@NotNull String sql, @Nullable Map<String, String> values, @Nullable Map<String, String> where, @Nullable Map<String, String> set) {
        try (Connection connection = connectionFactory.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(sql, values, where, set))) {
                plugin.debug(SqlStorage.class, "Run SQL:");
                plugin.debug(SqlStorage.class, sql);
                if (values != null)
                    plugin.debug(SqlStorage.class, values.toString());
                if (where != null)
                    plugin.debug(SqlStorage.class, where.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            Util.logSevereException(e);
        }
    }

}
