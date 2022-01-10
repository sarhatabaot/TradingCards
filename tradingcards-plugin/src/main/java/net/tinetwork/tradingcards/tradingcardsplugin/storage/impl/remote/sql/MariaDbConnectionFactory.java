package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql;

import com.zaxxer.hikari.HikariConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.StorageConfig;

/**
 * @author sarhatabaot
 */
//todo
public class MariaDbConnectionFactory extends HikariConnectionFactory {
    public MariaDbConnectionFactory(final StorageConfig storageConfig) {
        super(storageConfig);
    }

    @Override
    protected void configureDatabase(final HikariConfig config, final String address, final String port, final String databaseName, final String username, final String password) {

    }

    @Override
    public String getType() {
        return "mariadb";
    }
}
