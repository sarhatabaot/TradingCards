package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql;

import com.zaxxer.hikari.HikariConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.StorageConfig;

/**
 * @author sarhatabaot
 */
public class MySqlConnectionFactory extends HikariConnectionFactory{
    public MySqlConnectionFactory(final StorageConfig storageConfig) {
        super(storageConfig);
    }

    @Override
    protected void configureDatabase(final HikariConfig config, final String address, final String port, final String databaseName, final String username, final String password) {
        config.setJdbcUrl("jdbc:mysql://"+address+":"+port+"/"+databaseName);
        config.setUsername(username);
        config.setPassword(password);
    }


    @Override
    public String getType() {
        return "mysql";
    }
}
