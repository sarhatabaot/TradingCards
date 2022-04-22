package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.StorageConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.InternalExceptions;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.ResourceProvider;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.resource.LoadableResource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class HikariConnectionFactory implements ConnectionFactory{
    private final StorageConfig storageConfig;
    protected HikariDataSource dataSource;
    protected FluentConfiguration flywayConfig;

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
        LoggerFactory.getLogger(HikariConnectionFactory.class).info("Connected to database!");


        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .placeholders(Map.of("prefix",storageConfig.getTablePrefix(), "default_series_id",storageConfig.getDefaultSeriesId()))
                .load();

        try {
            flyway.baseline();
            LoggerFactory.getLogger(HikariConnectionFactory.class).info(Arrays.stream(flyway.info().all()).toString());
            flyway.migrate();
        } catch (FlywayException e) {
            LoggerFactory.getLogger(HikariConnectionFactory.class).error("FlywayError" ,e);
        }
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
            throw new SQLException(InternalExceptions.DATA_SOURCE_NULL);
        }

        Connection connection = this.dataSource.getConnection();
        if (connection == null) {
            throw new SQLException(InternalExceptions.GET_CONNECTION_NULL);
        }

        return connection;
    }


}
