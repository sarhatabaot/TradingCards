package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql;

import com.zaxxer.hikari.HikariConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.StorageConfig;

import java.sql.Connection;
import java.sql.SQLException;
public abstract class HikariConnectionFactory implements ConnectionFactory{
    private final StorageConfig storageConfig;

    public HikariConnectionFactory(final StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
    }

    /**
     * This may be different with every database type.
     * @param config hikari config
     * @param address address
     * @param port port
     * @param databaseName databaseName
     * @param username username
     * @param password password
     */
    protected abstract void configureDatabase(HikariConfig config, String address, String port, String databaseName, String username, String password);

    @Override
    public void init(final TradingCards plugin) {
        HikariConfig config = new HikariConfig();
        config.setPoolName("tradingcards-hikari");

        configureDatabase(config,storageConfig.getAddress(),storageConfig.getPort(),storageConfig.getDatabase(),storageConfig.getUsername(),storageConfig.getPassword());

        postInitialize();
    }

    /**
     * Called after the pool has been initialised
     */
    protected void postInitialize() {

    }

    @Override
    public void shutdown() throws Exception {

    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }


}
