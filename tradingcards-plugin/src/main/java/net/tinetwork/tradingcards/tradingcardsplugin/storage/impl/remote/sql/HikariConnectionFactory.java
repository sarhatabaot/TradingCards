package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.StorageConfig;

import java.sql.Connection;
import java.sql.SQLException;
public abstract class HikariConnectionFactory implements ConnectionFactory{
    private final StorageConfig storageConfig;
    private HikariDataSource dataSource;

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
        config.setInitializationFailTimeout(-1);

        this.dataSource = new HikariDataSource(config);

        postInitialize();
    }

    /**
     * Called after the pool has been initialised
     */
    protected void postInitialize() {

    }

    @Override
    public void shutdown() throws Exception {
        if (this.dataSource != null) {
            this.dataSource.close();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (this.dataSource == null) {
            throw new SQLException("Unable to get a connection from the pool. (datasource is null)");
        }

        Connection connection = this.dataSource.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }

        return connection;
    }


}
