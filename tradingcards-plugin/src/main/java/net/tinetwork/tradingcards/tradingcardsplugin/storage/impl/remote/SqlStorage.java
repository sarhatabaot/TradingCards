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
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.StorageType;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql.ConnectionFactory;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql.SchemaReader;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

    private static final String REWARDS_GET_BY_ID =
            "SELECT * FROM {prefix}rewards " +
                    "WHERE rarity_id=?" +
                    "ORDER BY command_order;";
    private static final String SERIES_SELECT_ALL =
            "SELECT * FROM {prefix}series;";

    private static final String SERIES_GET_BY_ID =
            "SELECT * FROM {prefix}series " +
                    "WHERE series_id=?;";

    private static final String COLOR_GET_BY_ID =
            "SELECT * FROM {prefix}series_colors "+
                    "WHERE series_id=?;";

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

    private static final String COLUMN_SERIES_ID = "series_id";
    private static final String COLUMN_MODE = "mode";

    private static final String COLUMN_COMMAND = "command";
    private static final String COLUMN_ORDER_NUMBER = "command_order";



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
        try (Connection connection = connectionFactory.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(DECKS_SELECT_ALL_BY_UUID, null,
                    Map.of(COLUMN_UUID, statementProcessor.wrap(playerUuid.toString()))))) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<Deck> decks = new ArrayList<>();
                    while (resultSet.next()) {
                        decks.add(getDeckFromResultSet(resultSet));
                    }
                    return decks;
                }
            }
        } catch (SQLException e) {
            this.plugin.getLogger().severe(e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public Deck getDeck(final UUID playerUuid, final int deckNumber) {
        try (Connection connection = connectionFactory.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(DECKS_SELECT_BY_DECK_NUMBER, null,
                    Map.of(COLUMN_UUID, statementProcessor.wrap(playerUuid.toString()),
                            COLUMN_DECK_NUMBER, String.valueOf(deckNumber))))) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return getDeckFromResultSet(resultSet);
                    }
                    if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                        this.plugin.debug(getClass(), "Could not find a deck for uuid=" + playerUuid + ",decknumber=" + deckNumber);
                        return new Deck(playerUuid, deckNumber, new ArrayList<>());
                    }
                }
            }
        } catch (SQLException e) {
            this.plugin.getLogger().severe(e.getMessage());
            return null;
        }
        this.plugin.getLogger().severe("Returning a null deck.");
        return null;
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
        try (Connection connection = connectionFactory.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statementProcessor.apply(RARITY_GET_BY_ID,null,
                    Map.of(COLUMN_RARITY_ID,rarityId)))) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if(resultSet.next()) {
                        final String displayName = resultSet.getString(COLUMN_DISPLAY_NAME);
                        final String defaultColor = resultSet.getString(COLUMN_DEFAULT_COLOR);
                        final List<String> rewards = getRewards(rarityId);
                        final double buyPrice = resultSet.getDouble(COLUMN_BUY_PRICE);
                        final double sellPrice = resultSet.getDouble(COLUMN_SELL_PRICE);
                        return new Rarity(rarityId,displayName,defaultColor,buyPrice,sellPrice,rewards);
                    }
                    if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                        this.plugin.getLogger().info("No such rarity "+rarityId);
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            Util.logSevereException(e);
        }
        return null;
    }

    @Override
    public List<String> getRewards(final String rarityId) {
        try(Connection connection = connectionFactory.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement(statementProcessor.apply(REWARDS_GET_BY_ID,null,
                    Map.of(COLUMN_RARITY_ID,rarityId)))) {
                try (ResultSet resultSet = preparedStatement.executeQuery()){
                    List<String> rewards = new ArrayList<>();
                    while(resultSet.next()) {
                        final String command = resultSet.getString(COLUMN_COMMAND);
                        rewards.add(command);
                    }

                    if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                        this.plugin.getLogger().info("No such rarity "+rarityId);
                        return Collections.emptyList();
                    }

                    return rewards;
                }
            }
        } catch (SQLException e) {
            Util.logSevereException(e);
        }
        return Collections.emptyList();
    }

    @Override
    public Series getSeries(final String seriesId) {
        return null;
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
            boolean cardExistsInDatabase = dbDeck.containsCard(deckEntry.getCardId(), deckEntry.getRarityId(),deckEntry.isShiny());
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
                plugin.debug(SqlStorage.class,"(UPDATE) "+ storageEntry);
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
                plugin.debug(SqlStorage.class,"(ADD) "+ entry);
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
                plugin.debug(SqlStorage.class,"(REMOVE) "+ entry);
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
        try (final Connection connection = connectionFactory.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(RARITY_SELECT_ALL,null,null))){
                try(final ResultSet resultSet = statement.executeQuery()) {
                    final List<Rarity> rarities = new ArrayList<>();
                    while(resultSet.next()) {
                        final String id = resultSet.getString(COLUMN_RARITY_ID);
                        final String displayName = resultSet.getString(COLUMN_DISPLAY_NAME);
                        final String defaultColor= resultSet.getString(COLUMN_DEFAULT_COLOR);
                        final double buyPrice = resultSet.getDouble(COLUMN_BUY_PRICE);
                        final double sellPrice = resultSet.getDouble(COLUMN_SELL_PRICE);
                        final List<String> rewards = getRewards(id);
                        rarities.add(new Rarity(id,displayName,defaultColor,buyPrice,sellPrice,rewards));
                    }
                    if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                        return Collections.emptyList();
                    }
                    return rarities;
                }
            }
        } catch (SQLException e) {
            Util.logSevereException(e);
        }
        return Collections.emptyList();
    }


    @Override
    public Collection<Series> getAllSeries() {
        try(final Connection connection = connectionFactory.getConnection()) {
            try(final PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(SERIES_SELECT_ALL,null,null))) {
                try (final ResultSet resultSet = statement.executeQuery()){
                    List<Series> series = new ArrayList<>();
                    while(resultSet.next()) {
                        final String id = resultSet.getString(COLUMN_SERIES_ID);
                        final String displayName = resultSet.getString(COLUMN_DISPLAY_NAME);
                        final Mode mode = Mode.getMode(resultSet.getString(COLUMN_MODE));
                        final ColorSeries colorSeries = getColorSeries(id);
                        series.add(new Series(id,mode,displayName,null,colorSeries));
                    }
                    if (resultSet.getFetchSize() == 0 || resultSet.wasNull()) {
                        return Collections.emptyList();
                    }
                    return series;
                }
            }
        } catch (SQLException e) {
            Util.logSevereException(e);
        }
        return null;
    }

    @Contract("_ -> new")
    private @NotNull ColorSeries getColorSeries(final String seriesId) {
        try(final Connection connection = connectionFactory.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(COLOR_GET_BY_ID, null, Map.of(COLUMN_SERIES_ID,seriesId)))) {
                try (final ResultSet resultSet = statement.executeQuery()) {
                    if(resultSet.next()) {
                        final String about = resultSet.getString("about");
                        final String info= resultSet.getString("info");
                        final String type= resultSet.getString("type");
                        final String rarity= resultSet.getString("rarity");
                        final String series= resultSet.getString("series");
                        return new ColorSeries(series,type,info,about,rarity);
                    }
                }
            }
        } catch (SQLException e) {
            Util.logSevereException(e);
        }
        return Util.DEFAULT_COLORS;
    }

    @Override
    public Set<Series> getActiveSeries() {
        return null;
    }

    @Override
    public Map<String, TradingCard> getCardsMap() {
        return null;
    }

    @Override
    public Map<String, TradingCard> getActiveCardsMap() {
        return null;
    }

    @Override
    public List<TradingCard> getCards() {
        return null;
    }

    @Override
    public List<TradingCard> getCardsInRarity(final String rarityId) {
        return null;
    }

    @Override
    public List<TradingCard> getCardsInSeries(final String seriesId) {
        return null;
    }

    @Override
    public List<TradingCard> getActiveCards() {
        return null;
    }

    @Override
    public Card<TradingCard> getCard(final String cardId, final String rarityId) {
        return null;
    }

    @Override
    public Card<TradingCard> getActiveCard(final String cardId, final String rarityId) {
        return null;
    }

    @Override
    public @Nullable Pack getPack(final String packsId) {
        return null;
    }

    @Override
    public List<Pack> getPacks() {
        return null;
    }

    @Override
    public Set<DropType> getDropTypes() {
        return null;
    }

    @Override
    public DropType getCustomType(final String typeId) {
        return null;
    }

    @Override
    public void createCard(final String cardId, final String rarityId, final String seriesId) {

    }

    @Override
    public void createRarity(final String rarityId) {

    }

    @Override
    public void createSeries(final String seriesId) {

    }

    @Override
    public void createCustomType(final String typeId, final String type) {

    }

    @Override
    public void createPack(final String packId) {

    }

    @Override
    public void reload() {
        //nothing to do here.
    }

    @Override
    public void editCardDisplayName(final String rarityId, final String cardId, final String seriesId, final String displayName) {


    }

    @Override
    public void editCardSeries(final String rarityId, final String cardId, final String seriesId, final Series value) {

    }

    @Override
    public void editCardSellPrice(final String rarityId, final String cardId, final String seriesId, final double value) {

    }

    @Override
    public void editCardType(final String rarityId, final String cardId, final String seriesId, final DropType value) {

    }

    @Override
    public void editCardInfo(final String rarityId, final String cardId, final String seriesId, final String value) {

    }

    @Override
    public void editCardCustomModelData(final String rarityId, final String cardId, final String seriesId, final int value) {

    }

    @Override
    public void editCardBuyPrice(final String rarityId, final String cardId, final String seriesId, final double value) {

    }

    @Override
    public void editRarityBuyPrice(final String rarityId, final double buyPrice) {

    }

    @Override
    public void editRarityAddReward(final String rarityId, final String reward) {

    }

    @Override
    public void editRarityDefaultColor(final String rarityId, final String defaultColor) {

    }

    @Override
    public void editRarityDisplayName(final String rarityId, final String displayName) {

    }

    @Override
    public void editRaritySellPrice(final String rarityId, final double sellPrice) {

    }

    @Override
    public void editRarityRemoveAllRewards(final String rarityId) {

    }

    @Override
    public void editRarityRemoveReward(final String rarityId, final int rewardNumber) {

    }

    @Override
    public void editSeriesDisplayName(final String seriesId, final String displayName) {

    }

    @Override
    public void editSeriesColors(final String seriesId, final ColorSeries colors) {

    }

    @Override
    public void editSeriesMode(final String seriesId, final Mode mode) {

    }

    @Override
    public void editCustomTypeDisplayName(final String typeId, final String displayName) {

    }

    @Override
    public void editCustomTypeType(final String typeId, final String type) {

    }

    @Override
    public void editPackDisplayName(final String packId, final String displayName) {

    }

    @Override
    public void editPackContents(final String packId, final int lineNumber, final Pack.PackEntry packEntry) {

    }

    @Override
    public void editPackPermission(final String packId, final String permission) {

    }

    @Override
    public void editPackPrice(final String packId, final double price) {

    }
}
