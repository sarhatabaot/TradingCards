package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql;

import com.zaxxer.hikari.HikariConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.StorageConfig;

import java.util.Map;

/**
 * @author sarhatabaot
 */
public class MariaDbConnectionFactory extends HikariConnectionFactory {
    public MariaDbConnectionFactory(final StorageConfig storageConfig) {
        super(storageConfig);
    }

    @Override
    protected void configureDatabase(final HikariConfig config, final String address, final String port, final String databaseName, final String username, final String password) {
        config.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        config.addDataSourceProperty("serverName", address);
        config.addDataSourceProperty("port", port);
        config.addDataSourceProperty("databaseName", databaseName);
        config.setUsername(username);
        config.setPassword(password);
    }

    @Override
    public String getType() {
        return "mariadb";
    }

    @Override
    protected void overrideProperties(final Map<String, String> properties) {
        //don't override anything
    }
}
