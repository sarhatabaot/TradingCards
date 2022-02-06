package net.tinetwork.tradingcards.api;

import net.tinetwork.tradingcards.api.blacklist.Blacklist;
import net.tinetwork.tradingcards.api.config.settings.GeneralConfigurate;
import net.tinetwork.tradingcards.api.config.settings.RarityConfigurate;
import net.tinetwork.tradingcards.api.config.settings.SeriesConfigurate;
import net.tinetwork.tradingcards.api.manager.CardManager;
import net.tinetwork.tradingcards.api.manager.DeckManager;
import net.tinetwork.tradingcards.api.manager.PackManager;
import net.tinetwork.tradingcards.api.manager.RarityManager;
import net.tinetwork.tradingcards.api.manager.TypeManager;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public abstract class TradingCardsPlugin<T> extends JavaPlugin {
    public TradingCardsPlugin() {
        super();
    }

    public TradingCardsPlugin(final @NotNull JavaPluginLoader loader, final @NotNull PluginDescriptionFile description, final @NotNull File dataFolder, final @NotNull File file) {
        super(loader, description, dataFolder, file);
    }

    /**
     * Send a debug message in the log.
     * @param message Message to send.
     */
    public abstract void debug(Class<?> clazz, String message);

    /**
     *
     * @param string type
     * @return If the type is a mob
     */
    public abstract boolean isMob(String string);


    /**
     *
     * @param type Type
     * @return If the type is a mob
     */
    public abstract boolean isMob(EntityType type);

    /**
     *
     * @return Get the card manager
     */
    public abstract CardManager<T> getCardManager();

    /**
     *
     * @return Get the pack manager
     */
    public abstract PackManager getPackManager();

    /**
     *
     * @return Get the deck manager
     */
    public abstract DeckManager getDeckManager();

    /**
     *
     * @return Player blacklist
     */
    public abstract Blacklist<Player> getPlayerBlacklist();


    /**
     *
     * @return The world blacklist
     */
    public abstract Blacklist<World> getWorldBlacklist();


    /**
     *
     * @return Get the plugin instance.
     */
    public abstract TradingCardsPlugin<T> get();

    public abstract GeneralConfigurate getGeneralConfig();

    public abstract RarityConfigurate getRaritiesConfig();

    public abstract SeriesConfigurate getSeriesConfig();

    public abstract TypeManager getDropTypeManager();

    public abstract RarityManager getRarityManager();
}
