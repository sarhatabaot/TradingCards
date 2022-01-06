package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.api.config.SimpleConfigurate;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.StorageType;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;

/**
 * @author sarhatabaot
 */
public class StorageConfig extends SimpleConfigurate {

    //TODO Since we are storing sensitive information, we should add a command that sends the info back to dev usage
    //TODO Something like /cards debug (zips all settings and modifies the password/username line to **
    private StorageType type;
    private String address;
    private String port;
    private String database;

    private String username;
    private String password; //this should not be accessible anywhere except when the db initializes.

    private String tablePrefix;


    public StorageConfig(final TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings" + File.separator, "storage.yml", "settings");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.type = StorageType.valueOf(rootNode.node("storage-type").getString("YAML"));

        final ConfigurationNode dataNode = rootNode.node("database");
        this.address = dataNode.node("address").getString("localhost");
        this.port = dataNode.node("port").getString("3306");
        this.database = dataNode.node("database").getString("minecraft");
        this.username = dataNode.node("username").getString("username");
        this.password = dataNode.node("password").getString();
        this.tablePrefix = dataNode.node("table-prefix").getString("tradingcards_");
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

    public String getPort() {
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
}
