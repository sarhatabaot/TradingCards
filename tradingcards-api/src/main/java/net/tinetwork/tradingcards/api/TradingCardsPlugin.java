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

    public abstract void debug(String message);

    public abstract boolean isMob(String string);

    public abstract boolean isMob(EntityType type);

    public abstract CardManager<T> getCardManager();

    public abstract PackManager getPackManager();

    public abstract DeckManager getDeckManager();

    public abstract Blacklist<Player> getPlayerBlacklist();

    public abstract Blacklist<World> getWorldBlacklist();

    public abstract TradingCardsPlugin<T> get();
}
