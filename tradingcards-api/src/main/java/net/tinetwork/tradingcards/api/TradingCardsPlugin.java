package net.tinetwork.tradingcards.api;

import net.tinetwork.tradingcards.api.blacklist.Blacklist;
import net.tinetwork.tradingcards.api.manager.CardManager;
import net.tinetwork.tradingcards.api.manager.DeckManager;
import net.tinetwork.tradingcards.api.manager.PackManager;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public abstract class TradingCardsPlugin<T> extends JavaPlugin {
    /**
     *
     * @param message
     */
    public abstract void debug(String message);

    /**
     *
     * @param string
     * @return
     */
    public abstract boolean isMob(String string);


    /**
     *
     * @param type
     * @return
     */
    public abstract boolean isMob(EntityType type);

    /**
     *
     * @return
     */
    public abstract CardManager<T> getCardManager();

    /**
     *
     * @return
     */
    public abstract PackManager getPackManager();

    /**
     *
     * @return
     */
    public abstract DeckManager getDeckManager();

    /**
     *
     * @return
     */
    public abstract Blacklist<Player> getPlayerBlacklist();


    /**
     *
     * @return
     */
    public abstract Blacklist<World> getWorldBlacklist();


    /**
     *
     * @return
     */
    public abstract TradingCardsPlugin<T> get();

    public TradingCardsPlugin() {
    }

    public TradingCardsPlugin(@NotNull JavaPluginLoader loader, @NotNull PluginDescriptionFile description, @NotNull File dataFolder, @NotNull File file) {
        super(loader, description, dataFolder, file);
    }
}
