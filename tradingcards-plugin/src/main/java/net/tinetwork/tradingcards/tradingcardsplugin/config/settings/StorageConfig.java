package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import com.lapzupi.dev.config.Transformation;
import net.tinetwork.tradingcards.api.config.settings.StorageConfigurate;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.config.transformations.StorageTransformations;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.settings.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.StorageType;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

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
    private String password;

    private String tablePrefix;

    private String defaultSeriesId;

    private String defaultCardsFile;

    private boolean firstTimeValues;


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

        final ConfigurationNode sql = rootNode.node("sql");
        this.firstTimeValues = sql.node("first-time-values").getBoolean(Storage.Sql.FIRST_TIME_VALUES);
    }
    
    @Override
    protected void builderOptions(TypeSerializerCollection.Builder builder) {
        //nothing toa dd
    }
    
    @Override
    protected Transformation getTransformation() {
        return new StorageTransformations();
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

    public boolean isFirstTimeValues() {
        return firstTimeValues;
    }

    @Override
    public String getDefaultCardsFile() {
        return defaultCardsFile;
    }
}
