package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote;

import com.google.common.collect.ImmutableMap;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.StorageType;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql.ConnectionFactory;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql.SchemaReader;

import java.io.IOException;
import java.io.InputStream;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author sarhatabaot
 */
public class SqlStorage implements Storage {
    private static String DECKS_SELECT_ALL_BY_UUID = "SELECT * FROM '{prefix}decks' WHERE uuid=?";
    private static String DECKS_SELECT_BY_DECK_NUMBER = "SELECT * FROM '{prefix}decks' WHERE uuid=? deck_number=?";
    private static String DECKS_SELECT_BY_CARD_AND_RARITY = "SELECT * FROM '{prefix}decks' WHERE uuid=? card_id=? rarity_id=?";
    private static String DECKS_SELECT_BY_CARD_AND_RARITY_SHINY = "SELECT * FROM '{prefix}decks' WHERE uuid=? card_id=? rarity_id=? is_shiny=true";
    private static String DECKS_INSERT_CARD = "INSERT INTO '{prefix}decks' (uuid, deck_number, card_id, rarity_id, amount, is_shiny, slot) VALUES (?,?,?,?,?,?,?) WHERE uuid=? deck_number=?";
    private static String DECKS_UPDATE_CARD = "UPDATE '{prefix}decks' (uuid, deck_number, card_id, rarity_id, amount, is_shiny) VALUES (?,?,?,?,?,?,?) WHERE uuid=? deck_number=? slot=?";
    private static String DECKS_REMOVE_CARD = "DELETE FROM '{prefix}decks' WHERE uuid=? deck_number=? card_id=? rarity_id=? is_shiny=? slot=?";
    private final TradingCards plugin;
    private final ConnectionFactory connectionFactory;
    private final StatementProcessor statementProcessor;


    @Override
    public void init(final TradingCards plugin) {
        connectionFactory.init(plugin);
    }

    public SqlStorage(final TradingCards plugin, final String tablePrefix, final ConnectionFactory connectionFactory) {
        this.plugin = plugin;
        this.connectionFactory = connectionFactory;
        this.statementProcessor = new StatementProcessor(tablePrefix);
        try {
            applySchema();
        } catch (SQLException|IOException e){
            plugin.getLogger().severe(e.getMessage());
        }
    }

    @Override
    public List<Deck> getPlayerDecks(final UUID playerUuid) {
        try (Connection connection = connectionFactory.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(DECKS_SELECT_ALL_BY_UUID, null,
                    Map.of("uuid", playerUuid.toString())))) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<Deck> decks = new ArrayList<>();
                    while(resultSet.next()) {
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
                    Map.of("uuid", playerUuid.toString(),
                            "deck_number", String.valueOf(deckNumber))))) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    return getDeckFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            this.plugin.getLogger().severe(e.getMessage());
        }
        return null;
    }

    private Deck getDeckFromResultSet(ResultSet resultSet) throws SQLException {
        final String playerUuid = resultSet.getString("uuid");
        final int deckNumber = resultSet.getInt("deck_number");
        List<StorageEntry> entries = new ArrayList<>();
        while(resultSet.next()) {
            final String rarityId = resultSet.getString("rarity_id");
            final String cardId=  resultSet.getString("card_id");
            final boolean isShiny = resultSet.getBoolean("is_shiny");
            final int amount = resultSet.getInt("amount");
            entries.add(new StorageEntry(rarityId,cardId,amount,isShiny));
        }
        return new Deck(UUID.fromString(playerUuid), deckNumber, entries);
    }

    @Override
    public void save(final UUID playerUuid, final int deckNumber, final Deck deck) {
        //save any queued up changes.
    }

    @Override
    public boolean hasCard(final UUID playerUuid, final String card, final String rarity) {
        try (Connection connection = connectionFactory.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(DECKS_SELECT_BY_CARD_AND_RARITY,null,
                    Map.of("uuid",playerUuid.toString(),
                            "card_id",card,
                            "rarity_id",rarity)))){
                try (ResultSet resultSet = statement.executeQuery()) {
                    //try and access a result, if it doesn't exist, return false.
                    String cardId = resultSet.getString("card_id"); //todo
                    plugin.debug(SqlStorage.class,cardId);
                    return !resultSet.wasNull();
                }
            }
        }catch (SQLException e){
            return false;
        }
    }

    @Override
    public boolean hasShinyCard(final UUID playerUuid, final String card, final String rarity) {
        try (Connection connection = connectionFactory.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(DECKS_SELECT_BY_CARD_AND_RARITY_SHINY,null,
                    Map.of("uuid",playerUuid.toString(),
                            "card_id",card,
                            "rarity_id",rarity,
                            "is_shiny", String.valueOf(true))))){
                try (ResultSet resultSet = statement.executeQuery()) {
                    //try and access a result, if it doesn't exist, return false.
                    String cardId = resultSet.getString("card_id"); //todo
                    plugin.debug(SqlStorage.class,cardId);
                    return !resultSet.wasNull();
                }
            }
        }catch (SQLException e){
            return false;
        }
    }

    @Override
    public StorageType getType() {
        return StorageType.MYSQL;
    }


    public void add(final UUID playerUuid, final int deckNumber, final String cardId, final String rarityId, final int amount, final boolean isShiny, final int slot) {
        try (Connection connection = connectionFactory.getConnection()) {
            ImmutableMap<String, String> values = statementProcessor.generateValuesMap(playerUuid, deckNumber, cardId, rarityId, amount, isShiny, slot);
            try (PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(DECKS_INSERT_CARD, values,
                    Map.of("uuid", playerUuid.toString(),
                            "deck_number", String.valueOf(deckNumber))))) {
                statement.execute();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    public void remove(final UUID playerUuid, final int deckNumber, final String cardId, final String rarityId, final int amount, final boolean isShiny, final int slot) {
        try (Connection connection = connectionFactory.getConnection()) {
            ImmutableMap<String, String> values = statementProcessor.generateValuesMap(playerUuid, deckNumber, cardId, rarityId, amount, isShiny, slot);
            try (PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(DECKS_REMOVE_CARD, values,
                    Map.of("uuid", playerUuid.toString(),
                            "deck_number", String.valueOf(deckNumber),
                            "card_id", cardId,
                            "rarity_id", rarityId,
                            "is_shiny", String.valueOf(isShiny),
                            "slot", String.valueOf(slot))))) {
                statement.execute();
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
                    .collect(Collectors.toList());
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

}
