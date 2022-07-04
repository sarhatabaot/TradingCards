package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote;

import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.EmptyPack;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalExceptions;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalLog;
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
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql.ConnectionFactory;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql.SchemaReader;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.JooqRecordUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Results;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.MappedTable;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.io.InputStream;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * @author sarhatabaot
 */
public class SqlStorage implements Storage<TradingCard> {
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

    public SqlStorage(final TradingCards plugin, final String tablePrefix, final String dbName, final ConnectionFactory connectionFactory, final StorageType storageType) {
        this.plugin = plugin;
        this.connectionFactory = connectionFactory;
        this.storageType = storageType;
        this.statementProcessor = new StatementProcessor(tablePrefix);
        this.jooqSettings = new Settings();
        initJooqSettings(tablePrefix, dbName);
    }

    private void initJooqSettings(final String tablePrefix, final String dbName) {
        jooqSettings.setExecuteLogging(true);
        jooqSettings.withRenderMapping(
                new RenderMapping().withSchemata(
                        new MappedSchema().withInput("minecraft")
                                .withOutput(dbName)
                                .withTables(
                                        new MappedTable().withInput("cards").withOutput(tablePrefix + "cards"),
                                        new MappedTable().withInput("rarities").withOutput(tablePrefix + "rarities"),
                                        new MappedTable().withInput("custom_types").withOutput(tablePrefix + "custom_types"),
                                        new MappedTable().withInput("packs").withOutput(tablePrefix + "packs"),
                                        new MappedTable().withInput("packs_content").withOutput(tablePrefix + "packs_content"),
                                        new MappedTable().withInput("series").withOutput(tablePrefix + "series"),
                                        new MappedTable().withInput("rewards").withOutput(tablePrefix + "rewards"),
                                        new MappedTable().withInput("series_colors").withOutput(tablePrefix + "series_colors"),
                                        new MappedTable().withInput("decks").withOutput(tablePrefix + "decks")
                                )
                )
        );
    }

    @Override
    public List<Deck> getPlayerDecks(final @NotNull UUID playerUuid) {
        return new ExecuteQuery<List<Deck>, Results>(this, jooqSettings) {
            @Override
            public List<Deck> onRunQuery(final DSLContext dslContext) {
                return getQuery(dslContext.select()
                        .from(Decks.DECKS)
                        .where(Decks.DECKS.UUID.eq(playerUuid.toString()))
                        .fetchMany());
            }


            @Override
            public @NotNull List<Deck> getQuery(final @NotNull Results results) {
                List<Deck> decks = new ArrayList<>();
                for (Result<Record> recordResult : results) {
                    decks.add(JooqRecordUtil.getDeckFromRecord(recordResult));
                }
                return decks;
            }

            @Override
            public List<Deck> empty() {
                return Collections.emptyList();
            }
        }.prepareAndRunQuery();
    }

    @Override
    public Deck getDeck(final @NotNull UUID playerUuid, final int deckNumber) {
        return new ExecuteQuery<Deck, Result<Record>>(this, jooqSettings) {
            @Override
            public Deck onRunQuery(final DSLContext dslContext) {
                return getQuery(dslContext.select().from(Decks.DECKS)
                        .where(Decks.DECKS.UUID.eq(playerUuid.toString())
                                .and(Decks.DECKS.DECK_NUMBER.eq(deckNumber))).fetch());
            }

            @Override
            public @NotNull Deck getQuery(final @NotNull Result<Record> recordResult) {
                if (recordResult.isEmpty()) {
                    plugin.debug(getClass(), InternalDebug.Sql.COULD_NOT_FIND_DECK.formatted(playerUuid,deckNumber));
                    return new Deck(playerUuid, deckNumber, new ArrayList<>());
                }
                return JooqRecordUtil.getDeckFromRecord(recordResult);
            }


            @Override
            public Deck empty() {
                return null;
            }
        }.prepareAndRunQuery();
    }

    @Override
    public @Nullable Rarity getRarityById(final String rarityId) {
        return new ExecuteQuery<Rarity, Record>(this, jooqSettings) {

            @Override
            public Rarity onRunQuery(final DSLContext dslContext) {
                Result<Record> result = dslContext.select()
                        .from(Rarities.RARITIES)
                        .where(Rarities.RARITIES.RARITY_ID.eq(rarityId))
                        .limit(1)
                        .fetch();
                if (result.isEmpty()) {
                    plugin.debug(SqlStorage.class, InternalDebug.NO_RARITY.formatted(rarityId));
                    return empty();
                }
                return getQuery(result.get(0));
            }

            @Override
            public @NotNull Rarity getQuery(final @NotNull Record result) {
                final String displayName = result.getValue(Rarities.RARITIES.DISPLAY_NAME);
                final String defaultColor = result.getValue(Rarities.RARITIES.DEFAULT_COLOR);
                final List<String> rewards = getRewards(rarityId);
                final double buyPrice = result.getValue(Rarities.RARITIES.BUY_PRICE);
                final double sellPrice = result.getValue(Rarities.RARITIES.SELL_PRICE);
                return new Rarity(rarityId, displayName, defaultColor, buyPrice, sellPrice, rewards);
            }

            @Contract(pure = true)
            @Override
            public @Nullable Rarity empty() {
                return null;
            }
        }.prepareAndRunQuery();
    }

    @Override
    public List<String> getRewards(final String rarityId) {
        return new ExecuteQuery<List<String>, Result<Record>>(this, jooqSettings) {

            @Override
            public List<String> onRunQuery(final DSLContext dslContext) {
                boolean doesExist = dslContext.fetchExists(dslContext.select()
                        .from(Rewards.REWARDS)
                        .where(Rewards.REWARDS.RARITY_ID.eq(rarityId)));
                if(!doesExist) {
                    return empty();
                }
                Result<Record> result = dslContext.select()
                        .from(Rewards.REWARDS)
                        .where(Rewards.REWARDS.RARITY_ID.eq(rarityId)).fetch();
                return getQuery(result);
            }

            @Override
            public @NotNull List<String> getQuery(final @NotNull Result<Record> result) {
                List<String> rewards = new ArrayList<>();
                for (Record recordResult : result) {
                    rewards.add(recordResult.getValue(Rewards.REWARDS.COMMAND));
                }

                return rewards;
            }

            @Contract(pure = true)
            @Override
            public @NotNull @Unmodifiable List<String> empty() {
                return Collections.emptyList();
            }
        }.prepareAndRunQuery();
    }

    @Override
    public Series getSeries(final String seriesId) {
        return new ExecuteQuery<Series, Result<Record>>(this, jooqSettings) {
            @Override
            public Series onRunQuery(final DSLContext dslContext) {
                Result<Record> result = dslContext.select()
                        .from(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES)
                        .where(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES.SERIES_ID.eq(seriesId))
                        .limit(1)
                        .fetch();
                if (result.isEmpty()) {
                    plugin.debug(SqlStorage.class, InternalDebug.NO_SERIES.formatted(seriesId));
                    return empty();
                }

                return getQuery(result);
            }

            @Override
            public @NotNull Series getQuery(final @NotNull Result<Record> result) {
                Record recordResult = result.get(0);
                return JooqRecordUtil.getSeriesFromRecord(recordResult, getColorSeries(seriesId));
            }

            @Contract(pure = true)
            @Override
            public @Nullable Series empty() {
                return null;
            }
        }.prepareAndRunQuery();
    }

    @Override
    public boolean containsSeries(final String seriesId) {
        return new ExecuteQuery<Boolean,Record>(this,jooqSettings) {
            @Override
            public Boolean onRunQuery(final DSLContext dslContext) throws SQLException {
                return dslContext.fetchExists(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES
                        ,net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES.SERIES_ID.eq(seriesId));
            }

            @Override
            public Boolean getQuery(final @NotNull Record result) throws SQLException {
                return empty();
            }

            @Override
            public Boolean empty() {
                return false;
            }
        }.prepareAndRunQuery();
    }

    public static class StorageEntryComparator implements Comparator<StorageEntry> {
        @Override
        //Implements a simple comparator to allow for sorting
        //Entries will be sorted by rarityid and then by card name
        public int compare(final @NotNull StorageEntry o1, final StorageEntry o2) {
            if (o1.equals(o2))
                return 0;
            if (o1.getCardId().equals(o2.getCardId()))
                return o1.getRarityId().compareTo(o2.getRarityId());
            return o1.getCardId().compareTo(o2.getCardId());
        }
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
        byte isShiny = toByte(storageEntry.isShiny());
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
                plugin.debug(SqlStorage.class, InternalDebug.Sql.UPDATE.formatted(storageEntry));
            }
        }.executeUpdate();
    }

    @Override
    public boolean hasCard(final UUID playerUuid, final String cardId, final String rarityId, final String seriesId) {
        return new ExecuteQuery<Boolean, Result<Record>>(this, jooqSettings) {
            @Override
            public Boolean onRunQuery(final DSLContext dslContext) {
                return getQuery(dslContext.select()
                        .from(Decks.DECKS)
                        .where(Decks.DECKS.UUID.eq(playerUuid.toString())
                                .and(Decks.DECKS.CARD_ID.eq(cardId)
                                        .and(Decks.DECKS.RARITY_ID.eq(rarityId))
                                        .and(Decks.DECKS.SERIES_ID.eq(seriesId))
                                        .and(Decks.DECKS.IS_SHINY.eq((byte) 0))))
                        .fetch());
            }

            @Override
            public @NotNull Boolean getQuery(final @NotNull Result<Record> result) {
                return result.isNotEmpty();
            }

            @Override
            public Boolean empty() {
                return false;
            }
        }.prepareAndRunQuery();
    }

    @Override
    public boolean hasShinyCard(final UUID playerUuid, final String cardId, final String rarityId,final String seriesId) {
        return new ExecuteQuery<Boolean, Result<Record>>(this, jooqSettings) {
            @Override
            public Boolean onRunQuery(final DSLContext dslContext) {
                return getQuery(dslContext.select()
                        .from(Decks.DECKS)
                        .where(Decks.DECKS.UUID.eq(playerUuid.toString())
                                .and(Decks.DECKS.CARD_ID.eq(cardId)
                                        .and(Decks.DECKS.RARITY_ID.eq(rarityId))
                                        .and(Decks.DECKS.SERIES_ID.eq(seriesId))
                                        .and(Decks.DECKS.IS_SHINY.eq((byte) 1))))
                        .fetch());
            }

            @Override
            public @NotNull Boolean getQuery(final @NotNull Result<Record> result) {
                return result.isNotEmpty();
            }

            @Override
            public Boolean empty() {
                return false;
            }
        }.prepareAndRunQuery();
    }

    @Override
    public StorageType getType() {
        return storageType;
    }


    public void addCardToDeck(final UUID playerUuid, final int deckNumber, final @NotNull StorageEntry entry) {
        final String cardId = entry.getCardId();
        final String rarityId = entry.getRarityId();
        final int amount = entry.getAmount();
        final byte isShiny = toByte(entry.isShiny());
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
                        .and(Decks.DECKS.IS_SHINY.eq(toByte(entry.isShiny()))).execute();
                plugin.debug(SqlStorage.class, InternalDebug.Sql.REMOVE.formatted(entry));
            }
        }.executeUpdate();
    }

    //From LuckPerms.
    private void applySchema() throws IOException, SQLException {
        List<String> statements;
        //TODO, this should be applied via flyway and not using the schema reader.
        String schemaFileName = "db/base/V0_" + this.connectionFactory.getType().toLowerCase(Locale.ROOT) + ".sql";
        try (InputStream is = this.plugin.getResource(schemaFileName)) {
            if (is == null) {
                throw new IOException(InternalExceptions.NO_SCHEMA.formatted(this.connectionFactory.getType()));
            }

            statements = SchemaReader.getStatements(is).stream()
                    .map(this.statementProcessor::applyPrefix)
                    .toList();
        }

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
        return new ExecuteQuery<List<Rarity>, Result<Record>>(this, jooqSettings) {
            @Override
            public List<Rarity> onRunQuery(final DSLContext dslContext) {
                Result<Record> result = dslContext.select()
                        .from(Rarities.RARITIES)
                        .orderBy(Rarities.RARITIES.CUSTOM_ORDER).fetch();
                if (result.isEmpty()) {
                    return empty();
                }
                return getQuery(result);
            }

            @Override
            public @NotNull List<Rarity> getQuery(final @NotNull Result<Record> result) {
                final List<Rarity> rarities = new ArrayList<>();
                for (Record recordResult : result) {
                    final String rarityId = recordResult.getValue(Rarities.RARITIES.RARITY_ID);
                    final String displayName = recordResult.getValue(Rarities.RARITIES.DISPLAY_NAME);
                    final String defaultColor = recordResult.getValue(Rarities.RARITIES.DEFAULT_COLOR);
                    final double buyPrice = (recordResult.getValue(Rarities.RARITIES.BUY_PRICE) == null ? 0.00D : recordResult.getValue(Rarities.RARITIES.BUY_PRICE));
                    final double sellPrice = (recordResult.getValue(Rarities.RARITIES.SELL_PRICE) == null ? 0.00D : recordResult.getValue(Rarities.RARITIES.SELL_PRICE));
                    final List<String> rewards = getRewards(rarityId);
                    rarities.add(new Rarity(rarityId, displayName, defaultColor, buyPrice, sellPrice, rewards));
                }
                return rarities;
            }

            @Contract(pure = true)
            @Override
            public @NotNull @Unmodifiable List<Rarity> empty() {
                return Collections.emptyList();
            }
        }.prepareAndRunQuery();
    }


    @Override
    public Collection<Series> getAllSeries() {
        return new ExecuteQuery<Collection<Series>, Result<Record>>(this, jooqSettings) {
            @Override
            public Collection<Series> onRunQuery(final DSLContext dslContext) {
                Result<Record> result = dslContext.select()
                        .from(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES)
                        .fetch();
                if (result.isEmpty()) {
                    return empty();
                }
                return getQuery(result);
            }

            @Override
            public Collection<Series> getQuery(final @NotNull Result<Record> result) {
                List<Series> series = new ArrayList<>();
                for (Record recordResult : result) {
                    series.add(JooqRecordUtil.getSeriesFromRecord(recordResult, getColorSeries(recordResult.getValue(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES.SERIES_ID))));
                }
                return series;
            }

            @Contract(pure = true)
            @Override
            public @NotNull @Unmodifiable Collection<Series> empty() {
                return Collections.emptyList();
            }
        }.prepareAndRunQuery();
    }

    @Contract("_ -> new")
    private @NotNull ColorSeries getColorSeries(final String seriesId) {
        return new ExecuteQuery<ColorSeries, Result<Record>>(this, jooqSettings) {
            @Override
            public ColorSeries onRunQuery(final DSLContext dslContext) {
                return getQuery(dslContext.select()
                        .from(SeriesColors.SERIES_COLORS)
                        .where(SeriesColors.SERIES_COLORS.SERIES_ID.eq(seriesId))
                        .limit(1)
                        .fetch());
            }

            @Override
            public ColorSeries getQuery(final @NotNull Result<Record> result) {
                if (result.isEmpty())
                    return empty();

                return JooqRecordUtil.getColorSeriesFromRecord(result.get(0));
            }

            @Override
            public ColorSeries empty() {
                return Util.DEFAULT_COLORS;
            }
        }.prepareAndRunQuery();
    }

    @Override
    public Set<Series> getActiveSeries() {
        return new ExecuteQuery<Set<Series>, Result<Record>>(this, jooqSettings) {
            @Override
            public Set<Series> onRunQuery(final DSLContext dslContext) {
                return getQuery(dslContext.select()
                        .from(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES)
                        .where(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES.SERIES_MODE.eq(SeriesSeriesMode.ACTIVE))
                        .fetch());
            }

            @Override
            public @NotNull Set<Series> getQuery(final @NotNull Result<Record> result) {
                if (result.isEmpty()) {
                    return empty();
                }

                final Set<Series> activeSeries = new HashSet<>();
                for (Record recordResult : result) {
                    final Series series = JooqRecordUtil.getSeriesFromRecord(recordResult, getColorSeries(recordResult.getValue(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES.SERIES_ID)));
                    activeSeries.add(series);
                }
                return activeSeries;
            }

            @Contract(pure = true)
            @Override
            public @NotNull @Unmodifiable Set<Series> empty() {
                return Collections.emptySet();
            }
        }.prepareAndRunQuery();
    }

    private @NotNull TradingCard getTradingCardFromRecord(@NotNull Record recordResult) {
        final String cardId = recordResult.getValue(Cards.CARDS.CARD_ID);
        final String displayName = recordResult.getValue(Cards.CARDS.DISPLAY_NAME);
        final Rarity rarity = getRarityById(recordResult.getValue(Cards.CARDS.RARITY_ID));
        final boolean hasShiny = toBoolean(JooqRecordUtil.getOrDefault(recordResult.get(Cards.CARDS.HAS_SHINY),(byte) 0));
        final Series series = getSeries(recordResult.getValue(Cards.CARDS.SERIES_ID));
        final String info = recordResult.getValue(Cards.CARDS.INFO);
        final int customModelData = JooqRecordUtil.getOrDefault(recordResult.getValue(Cards.CARDS.CUSTOM_MODEL_DATA),0);
        final double buyPrice = JooqRecordUtil.getOrDefault(recordResult.getValue(Cards.CARDS.BUY_PRICE),0D);
        final double sellPrice = JooqRecordUtil.getOrDefault(recordResult.getValue(Cards.CARDS.SELL_PRICE),0D);
        final DropType dropType = plugin.getDropTypeManager().getType(JooqRecordUtil.getOrDefault(recordResult.get(Cards.CARDS.TYPE_ID),"passive"));
        final TradingCard card = new TradingCard(cardId,plugin.getGeneralConfig().cardMaterial());
        card.displayName(displayName)
                .rarity(rarity)
                .hasShiny(hasShiny)
                .series(series)
                .type(dropType)
                .info(info)
                .customModelNbt(customModelData)
                .buyPrice(buyPrice)
                .sellPrice(sellPrice);
        return card;
    }



    @Override
    public List<TradingCard> getCards() {
        return new ExecuteQuery<List<TradingCard>, Result<Record>>(this, jooqSettings) {
            @Override
            public List<TradingCard> onRunQuery(final DSLContext dslContext) {
                return getQuery(dslContext.select()
                        .from(Cards.CARDS)
                        .fetch());
            }

            @Override
            public List<TradingCard> getQuery(final @NotNull Result<Record> result) {
                if (result.isEmpty()) {
                    return empty();
                }
                List<TradingCard> cards = new ArrayList<>();
                for (Record recordResult : result) {
                    final TradingCard card = getTradingCardFromRecord(recordResult);
                    cards.add(card);
                }

                return cards;
            }

            @Contract(pure = true)
            @Override
            public @NotNull @Unmodifiable List<TradingCard> empty() {
                return Collections.emptyList();
            }
        }.prepareAndRunQuery();
    }

    @Override
    public List<TradingCard> getCardsInRarity(final String rarityId) {
        return new ExecuteQuery<List<TradingCard>, Result<Record>>(this, jooqSettings) {
            @Override
            public List<TradingCard> onRunQuery(final DSLContext dslContext) {
                return getQuery(dslContext.select()
                        .from(Cards.CARDS)
                        .where(Cards.CARDS.RARITY_ID.eq(rarityId)).fetch());
            }

            @Override
            public @NotNull List<TradingCard> getQuery(final @NotNull Result<Record> result) {
                if (result.isEmpty()) {
                    plugin.debug(SqlStorage.class, InternalDebug.Sql.EMPTY_RESULT.formatted(rarityId));
                    return empty();
                }
                List<TradingCard> cards = new ArrayList<>();
                for (Record recordResult : result) {
                    final TradingCard card = getTradingCardFromRecord(recordResult);
                    cards.add(card);
                }

                return cards;
            }


            @Contract(pure = true)
            @Override
            public @NotNull @Unmodifiable List<TradingCard> empty() {
                return Collections.emptyList();
            }
        }.prepareAndRunQuery();
    }

    @Override
    public List<TradingCard> getCardsInSeries(final String seriesId) {
        return new ExecuteQuery<List<TradingCard>, Result<Record>>(this, jooqSettings) {

            @Override
            public @NotNull List<TradingCard> getQuery(final @NotNull Result<Record> result) {
                if (result.isEmpty()) {
                    return empty();
                }
                List<TradingCard> cards = new ArrayList<>();
                for (Record recordResult : result) {
                    final TradingCard card = getTradingCardFromRecord(recordResult);
                    cards.add(card);
                }
                return cards;
            }

            @Override
            public List<TradingCard> onRunQuery(final DSLContext dslContext) {
                return getQuery(dslContext.select()
                        .from(Cards.CARDS)
                        .where(Cards.CARDS.SERIES_ID.eq(seriesId))
                        .fetch());
            }


            @Contract(pure = true)
            @Override
            public @NotNull @Unmodifiable List<TradingCard> empty() {
                return Collections.emptyList();
            }
        }.prepareAndRunQuery();
    }

    @Override
    public List<TradingCard> getCardsInRarityAndSeries(final String rarityId, final String seriesId) {
        return new ExecuteQuery<List<TradingCard>, Result<Record>>(this, jooqSettings) {
            @Override
            public List<TradingCard> onRunQuery(final DSLContext dslContext) {
                return getQuery(dslContext.select()
                        .from(Cards.CARDS)
                        .where(Cards.CARDS.RARITY_ID.eq(rarityId)
                                .and(Cards.CARDS.SERIES_ID.eq(seriesId)))
                        .fetch());
            }

            @Override
            public @NotNull List<TradingCard> getQuery(final @NotNull Result<Record> result) {
                if (result.isEmpty()) {
                    return empty();
                }
                List<TradingCard> cards = new ArrayList<>();
                for (Record recordResult : result) {
                    final TradingCard card = getTradingCardFromRecord(recordResult);
                    cards.add(card);
                }
                return cards;
            }


            @Contract(pure = true)
            @Override
            public @NotNull @Unmodifiable List<TradingCard> empty() {
                return Collections.emptyList();
            }
        }.prepareAndRunQuery();
    }

    @Override
    public List<TradingCard> getActiveCards() {
        List<TradingCard> activeCards = new ArrayList<>();
        for (Series series : getActiveSeries()) {
            activeCards = Stream.concat(activeCards.stream(), getCardsInSeries(series.getId()).stream()).toList();
        }
        return activeCards;
    }

    public Card<TradingCard> getCard(final String cardId, final String rarityId) {
        return new ExecuteQuery<TradingCard, Result<Record>>(this, jooqSettings) {
            @Override
            public TradingCard onRunQuery(final DSLContext dslContext) {
                return getQuery(dslContext.select()
                        .from(Cards.CARDS)
                        .where(Cards.CARDS.CARD_ID.eq(cardId)
                                .and(Cards.CARDS.RARITY_ID.eq(rarityId)))
                        .limit(1)
                        .fetch());
            }

            @Override
            public TradingCard getQuery(final @NotNull Result<Record> result) {
                if (result.isEmpty()) {
                    return empty();
                }

                Record recordResult = result.get(0);
                return getTradingCardFromRecord(recordResult);
            }

            @Contract(" -> new")
            @Override
            public @NotNull TradingCard empty() {
                return new EmptyCard();
            }
        }.prepareAndRunQuery();
    }

    @Override
    public Card<TradingCard> getCard(final String cardId, final String rarityId, final String seriesId) {
        return new ExecuteQuery<TradingCard, Result<Record>>(this, jooqSettings) {
            @Override
            public TradingCard onRunQuery(final DSLContext dslContext) {
                return getQuery(dslContext.select()
                        .from(Cards.CARDS)
                        .where(Cards.CARDS.CARD_ID.eq(cardId)
                                .and(Cards.CARDS.RARITY_ID.eq(rarityId))
                                .and(Cards.CARDS.SERIES_ID.eq(seriesId)))
                        .limit(1)
                        .fetch());
            }

            @Override
            public TradingCard getQuery(final @NotNull Result<Record> result) {
                if (result.isEmpty()) {
                    return empty();
                }

                Record recordResult = result.get(0);
                return getTradingCardFromRecord(recordResult);
            }

            @Contract(" -> new")
            @Override
            public @NotNull TradingCard empty() {
                return new EmptyCard();
            }
        }.prepareAndRunQuery();
    }

    @Override
    public Pack getPack(final String packsId) {
        return new ExecuteQuery<Pack, Result<Record>>(this, jooqSettings) {
            @Override
            public Pack onRunQuery(final DSLContext dslContext) {
                return getQuery(dslContext.select()
                        .from(Packs.PACKS)
                        .where(Packs.PACKS.PACK_ID.eq(packsId))
                        .limit(1)
                        .fetch());
            }

            @Override
            public Pack getQuery(final @NotNull Result<Record> result) {
                if (result.isEmpty()) {
                    return empty();
                }
                Record recordResult = result.get(0);
                return JooqRecordUtil.getPackFromRecord(recordResult, getPackEntries(recordResult.getValue(Packs.PACKS.PACK_ID)));
            }

            @Contract(pure = true)
            @Override
            public @NotNull Pack empty() {
                return EmptyPack.emptyPack();
            }
        }.prepareAndRunQuery();
    }


    private @NotNull @Unmodifiable List<Pack.PackEntry> getPackEntries(final String packId) {
        return new ExecuteQuery<List<Pack.PackEntry>, Result<Record>>(this, jooqSettings) {

            @Override
            public List<Pack.PackEntry> onRunQuery(final DSLContext dslContext) {
                return getQuery(dslContext.select()
                        .from(PacksContent.PACKS_CONTENT)
                        .where(PacksContent.PACKS_CONTENT.PACK_ID.eq(packId))
                        .fetch());
            }

            @Override
            public @NotNull List<Pack.PackEntry> getQuery(final @NotNull Result<Record> result) {
                if (result.isEmpty()) {
                    return empty();
                }
                List<Pack.PackEntry> entries = new ArrayList<>();
                for (Record recordResult : result) {
                    int lineNumber = recordResult.getValue(PacksContent.PACKS_CONTENT.LINE_NUMBER);
                    entries.add(lineNumber, JooqRecordUtil.getPackEntryFromResult(recordResult));
                }
                return entries;
            }

            @Contract(pure = true)
            @Override
            public @NotNull @Unmodifiable List<Pack.PackEntry> empty() {
                return Collections.emptyList();
            }
        }.prepareAndRunQuery();
    }

    @Override
    public List<Pack> getPacks() {
        return new ExecuteQuery<List<Pack>, Result<Record>>(this, jooqSettings) {
            @Override
            public List<Pack> onRunQuery(final DSLContext dslContext) {
                Result<Record> result = dslContext.select()
                        .from(Packs.PACKS)
                        .fetch();
                return getQuery(result);
            }

            @Override
            public @NotNull List<Pack> getQuery(final @NotNull Result<Record> result) {
                if (result.isEmpty()) {
                    return empty();
                }

                List<Pack> packs = new ArrayList<>();
                for (Record recordResult : result) {
                    packs.add(JooqRecordUtil.getPackFromRecord(recordResult, getPackEntries(recordResult.getValue(Packs.PACKS.PACK_ID))));
                }
                return packs;
            }

            @Contract(pure = true)
            @Override
            public @NotNull @Unmodifiable List<Pack> empty() {
                return Collections.emptyList();
            }
        }.prepareAndRunQuery();
    }

    @Override
    public int getRarityCustomOrder(final String rarityId) {
        return new ExecuteQuery<Integer,Result<Record>>(this,jooqSettings){
            @Override
            public Integer onRunQuery(final DSLContext dslContext) throws SQLException {
                return getQuery(dslContext.select()
                        .from(Rarities.RARITIES)
                        .where(Rarities.RARITIES.RARITY_ID.eq(rarityId))
                        .fetch());
            }

            @Override
            public Integer getQuery(final @NotNull Result<Record> result) throws SQLException {
                if(result.isEmpty())
                    return empty();

                return result.get(0).get(Rarities.RARITIES.CUSTOM_ORDER);
            }

            @Override
            public Integer empty() {
                return 0;
            }
        }.prepareAndRunQuery();
    }

    @Override
    public Set<DropType> getDropTypes() {
        return new ExecuteQuery<Set<DropType>, Result<Record>>(this, jooqSettings) {
            @Override
            public Set<DropType> onRunQuery(final DSLContext dslContext) {
                return getQuery(dslContext.select().from(CustomTypes.CUSTOM_TYPES).fetch());
            }

            @Override
            public @NotNull Set<DropType> getQuery(final @NotNull Result<Record> result) {
                Set<DropType> customTypes = new HashSet<>();
                if (result.isEmpty()) {
                    return Collections.emptySet();
                }

                for (Record recordResult : result) {
                    DropType dropType = JooqRecordUtil.getDropTypeFromRecord(recordResult);
                    customTypes.add(dropType);
                }

                return customTypes;
            }

            @Override
            public Set<DropType> empty() {
                return Collections.emptySet();
            }
        }.prepareAndRunQuery();
    }

    @Override
    public DropType getCustomType(final String typeId) {
        return new ExecuteQuery<DropType, Result<Record>>(this, jooqSettings) {
            @Override
            public DropType onRunQuery(final DSLContext dslContext) {
                return getQuery(dslContext.select()
                        .from(CustomTypes.CUSTOM_TYPES)
                        .where(CustomTypes.CUSTOM_TYPES.TYPE_ID.eq(typeId))
                        .limit(1)
                        .fetch());
            }

            @Override
            public @NotNull DropType getQuery(final @NotNull Result<Record> result) {
                return JooqRecordUtil.getDropTypeFromRecord(result.get(0));
            }

            @Contract(pure = true)
            @Override
            public @Nullable DropType empty() {
                return null;
            }
        }.prepareAndRunQuery();
    }

    @Override
    public void reload() {
        try {
            shutdown();
            plugin.getLogger().warning(() -> InternalLog.Reload.SQL);
        } catch (Exception e){
            return;
        }

        connectionFactory.init(plugin);
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
                        .onDuplicateKeyIgnore()
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
                        .onDuplicateKeyIgnore()
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void createColorSeries(final String seriesId) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.insertInto(SeriesColors.SERIES_COLORS)
                        .set(SeriesColors.SERIES_COLORS.SERIES_ID, seriesId)
                        .onDuplicateKeyIgnore()
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editColorSeries(final String seriesId, final ColorSeries colors) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(SeriesColors.SERIES_COLORS)
                        .set(SeriesColors.SERIES_COLORS.SERIES, colors.getSeries())
                        .set(SeriesColors.SERIES_COLORS.ABOUT, colors.getAbout())
                        .set(SeriesColors.SERIES_COLORS.INFO, colors.getInfo())
                        .set(SeriesColors.SERIES_COLORS.TYPE, colors.getType())
                        .set(SeriesColors.SERIES_COLORS.RARITY, colors.getRarity())
                        .where(SeriesColors.SERIES_COLORS.SERIES_ID.eq(seriesId))
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
                        .set(CustomTypes.CUSTOM_TYPES.TYPE_ID, typeId)
                        .set(CustomTypes.CUSTOM_TYPES.DROP_TYPE, CustomTypesDropType.lookupLiteral(type))
                        .onDuplicateKeyIgnore()
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
                        .onDuplicateKeyIgnore()
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
                        .set(Cards.CARDS.SERIES_ID, value.getId())
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
    public void editCardHasShiny(final String rarityId, final String cardId, final String seriesId, final boolean value) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Cards.CARDS)
                        .set(Cards.CARDS.HAS_SHINY, toByte(value))
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
    public void editRarityCustomOrder(final String rarityId, final int customOrder) {
        new ExecuteUpdate(this,jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Rarities.RARITIES)
                        .set(Rarities.RARITIES.CUSTOM_ORDER, customOrder)
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
    public void editCustomTypeType(final String customTypeId, final String defaultTypeId) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                plugin.debug(SqlStorage.class, defaultTypeId);
                plugin.debug(SqlStorage.class, CustomTypesDropType.lookupLiteral(defaultTypeId).toString());
                dslContext.update(CustomTypes.CUSTOM_TYPES)
                        .set(CustomTypes.CUSTOM_TYPES.DROP_TYPE, CustomTypesDropType.lookupLiteral(defaultTypeId))
                        .where(CustomTypes.CUSTOM_TYPES.TYPE_ID.eq(customTypeId)).execute();
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
                dslContext.deleteFrom(PacksContent.PACKS_CONTENT)
                        .where(PacksContent.PACKS_CONTENT.PACK_ID.eq(packId)
                                .and(PacksContent.PACKS_CONTENT.LINE_NUMBER.eq(lineNumber)))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editPackPermission(final String packId, final String permission) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Packs.PACKS)
                        .set(Packs.PACKS.PERMISSION, permission)
                        .where(Packs.PACKS.PACK_ID.eq(packId))
                        .execute();
            }
        }.executeUpdate();
    }

    @Override
    public void editPackPrice(final String packId, final double price) {
        new ExecuteUpdate(this, jooqSettings) {
            @Override
            protected void onRunUpdate(final DSLContext dslContext) {
                dslContext.update(Packs.PACKS)
                        .set(Packs.PACKS.BUY_PRICE, price)
                        .where(Packs.PACKS.PACK_ID.eq(packId))
                        .execute();
            }
        }.executeUpdate();
    }

    @Contract(pure = true)
    private byte toByte(boolean value) {
        return (byte) (value ? 1 : 0);
    }

    private boolean toBoolean(byte value) {
        return value != 0;
    }

    @Override
    public int getCardsCount() {
        return new ExecuteQuery<Integer, Record>(this,jooqSettings) {
            @Override
            public Integer onRunQuery(final DSLContext dslContext) {
                return dslContext.fetchCount(Cards.CARDS);
            }

            @Override
            public Integer getQuery(final @NotNull Record result) {
                return null;
            }

            @Override
            public Integer empty() {
                return 0;
            }
        }.prepareAndRunQuery();
    }

    @Override
    public int getCardsInRarityCount(final String rarityId) {
        return new ExecuteQuery<Integer, Record>(this,jooqSettings) {
            @Override
            public Integer onRunQuery(final DSLContext dslContext) {
                return dslContext.fetchCount(Cards.CARDS, DSL.and(Cards.CARDS.RARITY_ID.eq(rarityId)));
            }

            @Override
            public Integer getQuery(final @NotNull Record result) {
                return null;
            }

            @Override
            public Integer empty() {
                return 0;
            }
        }.prepareAndRunQuery();
    }

    @Override
    public int getCardsInRarityAndSeriesCount(final String rarityId, final String seriesId) {
        return new ExecuteQuery<Integer, Record>(this,jooqSettings) {
            @Override
            public Integer onRunQuery(final DSLContext dslContext) {
                return dslContext.fetchCount(Cards.CARDS, DSL.and(Cards.CARDS.RARITY_ID.eq(rarityId).and(Cards.CARDS.SERIES_ID.eq(seriesId))));
            }

            @Override
            public Integer getQuery(final @NotNull Record result) {
                return null;
            }

            @Override
            public Integer empty() {
                return 0;
            }
        }.prepareAndRunQuery();
    }

    @Override
    public void shutdown() throws Exception {
        this.getConnectionFactory().shutdown();
    }
}
