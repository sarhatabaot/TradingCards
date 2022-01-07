package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql;

import com.zaxxer.hikari.HikariConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.StorageConfig;

import java.util.Map;

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

    @Override
    protected void overrideProperties(final Map<String, String> properties) {
        properties.putIfAbsent("cachePrepStmts", "true");
        properties.putIfAbsent("prepStmtCacheSize", "250");
        properties.putIfAbsent("prepStmtCacheSqlLimit", "2048");
        properties.putIfAbsent("useServerPrepStmts", "true");
        properties.putIfAbsent("useLocalSessionState", "true");
        properties.putIfAbsent("rewriteBatchedStatements", "true");
        properties.putIfAbsent("cacheResultSetMetadata", "true");
        properties.putIfAbsent("cacheServerConfiguration", "true");
        properties.putIfAbsent("elideSetAutoCommits", "true");
        properties.putIfAbsent("maintainTimeStats", "false");
        properties.putIfAbsent("alwaysSendSetIsolation", "false");
        properties.putIfAbsent("cacheCallableStmts", "true");

        // https://stackoverflow.com/a/54256150
        properties.putIfAbsent("serverTimezone", "UTC");

        super.overrideProperties(properties);
    }
}
