package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import com.github.sarhatabaot.kraken.core.config.ConfigurateFile;
import net.tinetwork.tradingcards.api.TradingCardsPlugin;
import net.tinetwork.tradingcards.api.config.settings.StorageConfigurate;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.settings.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.StorageType;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;

/**
 * @author sarhatabaot
 */
public class StorageConfig extends StorageConfigurate {
    private StorageType type;
    private String address;
    private int port;
    private String database;

    private String username;
    private String password; //this should not be accessible anywhere except when the db initializes.

    private String tablePrefix;

    private String defaultSeriesId;

    private String defaultCardsFile;


    public StorageConfig(final TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings" + File.separator, "storage.yml", "settings");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.type = StorageType.valueOf(rootNode.node("storage-type").getString(Storage.STORAGE_TYPE));

        final ConfigurationNode dataNode = rootNode.node("database");
        this.address = dataNode.node("address").getString(Storage.Database.ADDRESS);
        this.port = dataNode.node("port").getInt(Storage.Database.PORT);
        this.database = dataNode.node("database").getString(Storage.Database.DATABASE);
        this.username = dataNode.node("username").getString(Storage.Database.USERNAME);
        this.password = dataNode.node("password").getString(Storage.Database.PASSWORD);
        this.tablePrefix = dataNode.node("table-prefix").getString(Storage.Database.TABLE_PREFIX);

        final ConfigurationNode yaml = rootNode.node("yaml");
        this.defaultCardsFile = yaml.node("default-file").getString(Storage.Yaml.DEFAULT_FILE);

        final ConfigurationNode dbMigration = rootNode.node("database-migration");
        this.defaultSeriesId = dbMigration.node("default-series-id").getString(Storage.DatabaseMigration.DEFAULT_SERIES_ID);
    }

    @Override
    protected void preLoaderBuild() {

    }

    public StorageType getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public String getDefaultSeriesId() {
        return defaultSeriesId;
    }

    @Override
    public String getDefaultCardsFile() {
        return defaultCardsFile;
    }
}
