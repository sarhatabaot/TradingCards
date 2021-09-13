package net.tinetwork.tradingcards.api;

import net.tinetwork.tradingcards.api.blacklist.Blacklist;
import net.tinetwork.tradingcards.api.manager.CardManager;
import net.tinetwork.tradingcards.api.manager.DeckManager;
import net.tinetwork.tradingcards.api.manager.PackManager;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class TradingCardsPlugin<T> extends JavaPlugin {
    /**
     * Send a debug message in the log.
     * @param message Message to send.
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
}
