package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.StorageConfig;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class HikariConnectionFactory implements ConnectionFactory{
    private final StorageConfig storageConfig;
    private HikariDataSource dataSource;

    protected HikariConnectionFactory(final StorageConfig storageConfig) {
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

        Map<String,String> properties = new HashMap<>();

        overrideProperties(properties);
        setProperties(config,properties);

        this.dataSource = new HikariDataSource(config);
        postInitialize();
    }

    /**
     * Called after the pool has been initialised
     */
    @SuppressWarnings("EmptyMethod")
    protected void postInitialize() {
        //can be empty
    }

    //LP
    protected void overrideProperties(@NotNull Map<String,String> properties) {
        properties.putIfAbsent("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
    }

    //LP
    protected void setProperties(HikariConfig config, @NotNull Map<String, String> properties) {
        for (Map.Entry<String, String> property : properties.entrySet()) {
            config.addDataSourceProperty(property.getKey(), property.getValue());
        }
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
