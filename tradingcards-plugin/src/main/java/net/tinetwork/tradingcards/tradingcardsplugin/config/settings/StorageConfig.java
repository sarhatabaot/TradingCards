package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.api.TradingCardsPlugin;
import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.config.SimpleConfigurate;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.StorageType;
import org.spongepowered.configurate.ConfigurateException;

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


    public StorageConfig(final TradingCardsPlugin<? extends Card<?>> plugin) throws ConfigurateException {
        super(plugin, "settings" + File.separator + "storage.yml", "storage.yml", "settings");
    }

    @Override
    protected void initValues() throws ConfigurateException {

    }

    @Override
    protected void preLoaderBuild() {

    }

    public StorageType getType() {
        return type;
    }
}
